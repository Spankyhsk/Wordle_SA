package model

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import model.FileIOComponent.FileIOInterface
import model.persistenceComponent.PersistenceInterface
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.auto._
import model.GameInterface

import scala.concurrent.{ExecutionContextExecutor, Future}

case class ModelCommand(action: String, data: Map[String, Json])
case class ResultEvent(action: String, data: Map[String, Json])

class ModelService(using var game: GameInterface, var fileIO: FileIOInterface, var db: PersistenceInterface) {

  implicit val system: ActorSystem = ActorSystem("ModelService")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val kafkaBootstrap =
    if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092" else "localhost:29092"
  println(s"[ModelService] Kafka Bootstrap Server: $kafkaBootstrap")
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrap)
    .withGroupId("model-command-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrap)

  val kafkaProducer = producerSettings.createKafkaProducer()

  def sendResultEvent(action: String, data: Map[String, Json]): Unit = {
    val message = ResultEvent(action, data)
    val record = new org.apache.kafka.clients.producer.ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
    kafkaProducer.send(record)
  }

  def extractInt(data: Map[String, Json], key: String): Either[String, Int] =
    data.get(key).flatMap(_.asNumber.flatMap(_.toInt)).toRight(s"Feld '$key' fehlt oder ist kein Int")

  def extractString(data: Map[String, Json], key: String): Either[String, String] =
    data.get(key).flatMap(_.asString).toRight(s"Feld '$key' fehlt oder ist kein String")

  def extractMapIntString(json: Json): Either[String, Map[Int, String]] =
    json.asObject.map(_.toMap.collect {
      case (k, v) if v.isString => k.toInt -> v.asString.get
    }).toRight("Feedback fehlt oder nicht im richtigen Format")

  Consumer
    .committableSource(consumerSettings, Subscriptions.topics("model-commands"))
    .mapAsync(1) { msg =>
      val jsonString = msg.record.value()
      println(s"[Kafka] Empfange Aktion: $jsonString")

      decode[ModelCommand](jsonString) match {
        case Right(cmd) =>
          println(s"[Kafka] Bearbeite Aktion: ${cmd.action}")
          val futureResult: Future[_] = cmd.action match {
            case "createwinningboard" => 
              println("createWinningboard wird in Modelservice getriggert")
              Future(game.createwinningboard())

            case "setN" =>
              println(s"[Kafka] setN mit Daten: ${cmd.data}")
              extractInt(cmd.data, "versuche")
                .fold(err => Future.failed(new RuntimeException(err)), versuche => Future(game.setN(versuche)))

            case "createGameboard" => Future(game.createGameboard())

            case "changeState" =>
              extractInt(cmd.data, "level")
                .map(game.changeState)
                .fold(err => Future.failed(new RuntimeException(err)), Future.successful)

            case "save" => Future(fileIO.save(game))

            case "load" => Future {
              val result = fileIO.load(game)
              sendResultEvent("load", Map("result" -> Json.fromString(result)))
            }

            case "putGame" =>
              extractString(cmd.data, "name")
                .map(db.save(game, _))
                .fold(err => Future.failed(new RuntimeException(err)), Future.successful)

            case "getGame" =>
              cmd.data.get("gameId").flatMap(_.asNumber.flatMap(_.toLong)) match {
                case Some(gameId) => Future(db.load(gameId, game))
                case None => Future.failed(new RuntimeException("gameId fehlt oder ist kein Long"))
              }

            case "search" => Future {
              val result = db.search()
              sendResultEvent("search", Map("result" -> Json.fromString(result)))
            }

            case "step" =>
              (extractInt(cmd.data, "key"), cmd.data.get("feedback").flatMap(extractMapIntString(_).toOption)) match {
                case (Right(key), Some(feedback)) => Future(game.setRGameboard(key, feedback))
                case _ => Future.failed(new RuntimeException("Ungültiger step"))
              }

            case "undoStep" =>
              (extractInt(cmd.data, "key"), cmd.data.get("feedback").flatMap(extractMapIntString(_).toOption)) match {
                case (Right(key), Some(feedback)) => Future(game.undoStep(key, feedback))
                case _ => Future.failed(new RuntimeException("Ungültiger undoStep"))
              }

            case "count" => Future {
              sendResultEvent("count", Map("continue" -> Json.fromBoolean(game.count())))
            }

            case "getN" => Future {
              sendResultEvent("getN", Map("result" -> Json.fromInt(game.getN())))
            }

            case "gameboard" => Future {
              sendResultEvent("gameboard", Map("gameboard" -> Json.fromString(game.toString)))
            }

            case "targetwordToString" => Future {
              sendResultEvent("TargetwordToString", Map("targetWord" -> Json.fromString(game.TargetwordToString())))
            }

            case "guessTransform" => extractString(cmd.data, "guess") match {
              case Right(guess) => Future {
                println(s"[Kafka] Received guess in ModelService: $guess")
                println(s"[Kafka] cmd.data: ${cmd.data}")
                val map = Map("transformedGuess" -> Json.fromString(game.GuessTransform(guess)))
                sendResultEvent("GuessTransform", map)
                println(s"[Kafka] Transformed guess sent: ${map("transformedGuess")}")
              }
              case Left(err) => Future.failed(new RuntimeException(err))
            }

            case "controllLength" => extractInt(cmd.data, "length") match {
              case Right(length) => Future {
                sendResultEvent("controllLength", Map("result" -> Json.fromBoolean(game.controllLength(length))))
              }
              case Left(err) => Future.failed(new RuntimeException(err))
            }

            case "controllRealWord" => extractString(cmd.data, "guess") match {
              case Right(guess) => Future {
                sendResultEvent("controllRealWord", Map("result" -> Json.fromBoolean(game.controllRealWord(guess))))
              }
              case Left(err) => Future.failed(new RuntimeException(err))
            }

            case "evaluateGuess" => extractString(cmd.data, "guess") match {
              case Right(guess) => Future {
                val result = game.evaluateGuess(guess)
                val jsonResult = result.map { case (k, v) => k.toString -> Json.fromString(v) }
                sendResultEvent("evaluateGuess", Map("result" -> Json.obj(jsonResult.toSeq: _*)))
              }
              case Left(err) => Future.failed(new RuntimeException(err))
            }

            case other => Future.failed(new RuntimeException(s"Unbekannte Aktion: $other"))
          }

          futureResult.map(_ => msg.committableOffset.commitScaladsl())

        case Left(err) =>
          println(s"[Kafka] ❌ Fehler beim JSON-Parsing: $err")
          msg.committableOffset.commitScaladsl() // Optional: commit trotzdem
      }
    }
    .runWith(Sink.ignore)
}
