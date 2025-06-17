package controller

import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.ProducerRecord

case class ModelCommand(action: String, data: Map[String, Json])
case class ResultEvent(action: String, data: Map[String, Json])

object JsonFormats {
  implicit val decoder: Decoder[ModelCommand] = deriveDecoder[ModelCommand]
}

class AlpakkaController {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  //Note: getTargetword -> getTargetwordString
  val kafkaBootstrap = if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092" else "localhost:29092"
    
  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrap)

  // Kafka Consumer Einstellungen
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrap)
    .withGroupId("controller-result-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val resultCache = TrieMap.empty[String, ResultEvent]

  val kafkaProducer = producerSettings.createKafkaProducer()

  Consumer.plainSource(consumerSettings, Subscriptions.topics("model-results"))
    .map { msg =>
      decode[ResultEvent](msg.value()) match {
        case Right(result) =>
          println(s"Ergebnis vom Model empfangen: $result")
          resultCache.put(result.action, result)
        case Left(error) =>
          println(s"Fehler beim Parsen von JSON im Controller: $error")
      }
    }
    .runWith(Sink.ignore)
  
  def send:Unit(record: ProducerRecord[String, String]){
    kafkaProducer.send(record)
  }
}
