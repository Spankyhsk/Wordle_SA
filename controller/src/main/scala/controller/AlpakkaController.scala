package controller

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.{Materializer}
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContextExecutor
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import util.Event // dein Enum, z. B. Event.GameStarted, etc.
import ModelCommand._
import ResultEvent._

import io.circe.generic.semiauto._

case class ModelCommand(action: String, data: Map[String, io.circe.Json])

object ModelCommand {
  implicit val decoder: Decoder[ModelCommand] = deriveDecoder[ModelCommand]
}

case class ResultEvent(action: String, data: Map[String, io.circe.Json])

object ResultEvent {
  implicit val decoder: Decoder[ResultEvent] = deriveDecoder[ResultEvent]
}


class AlpakkaController {
  // --- Akka & Materializer Setup ---
  implicit val system: ActorSystem = ActorSystem("AlpakkaController")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // --- Kafka Bootstrap Server (Docker-aware) ---
  val kafkaBootstrap: String =
    if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092"
    else "localhost:29092"

  // --- Kafka Producer/Consumer Settings ---
  private val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrap)

  private val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrap)
    .withGroupId("controller-result-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  private val kafkaProducer = producerSettings.createKafkaProducer()

  // --- Ergebnis-Speicher ---
  val resultCache: TrieMap[String, ResultEvent] = TrieMap.empty

  // --- Kafka Consumer: Ergebnisse vom Model empfangen ---
  Consumer
    .plainSource(consumerSettings, Subscriptions.topics("model-results"))
    .map { msg =>
      decode[ResultEvent](msg.value()) match {
        case Right(result) =>
          println(s" Ergebnis vom Model empfangen: $result")
          resultCache.put(result.action, result) // oder ein anderer eindeutiger Key
        case Left(error) =>
          println(s"Fehler beim Parsen von JSON im Controller: $error\nPayload war: ${msg.value()}")
      }
    }
    .runWith(Sink.ignore)

  // --- Nachrichten an Kafka senden ---
  def send(record: ProducerRecord[String, String]): Unit =
    kafkaProducer.send(record)

  // --- UI-Events senden ---
  def sendUiEvent(eventType: Event): Unit = {
    val uiEventJson = Json.obj(
      "eventType" -> Json.fromString(eventType.toString)
    )
    val record = new ProducerRecord[String, String]("ui-events", uiEventJson.noSpaces)
    kafkaProducer.send(record)
  }
}
