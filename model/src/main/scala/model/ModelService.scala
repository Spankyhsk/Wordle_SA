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
import play.api.libs.json.JsObject
import io.circe.*
import io.circe.generic.semiauto.*
import io.circe.parser.*
import model.JsonFormats.decoder
import org.apache.kafka.clients.producer.ProducerRecord
import io.circe.syntax._ // für .asJson
import io.circe.generic.auto._ // erstellt Encoder/Decoder automatisch

import scala.concurrent.{ExecutionContextExecutor, Future}

case class ModelCommand(action:String, data: Map[String, Json])
case class ResultEvent(action: String, data:Map[String, Json])

object JsonFormats {
  implicit val decoder: Decoder[ModelCommand] = deriveDecoder[ModelCommand]
}

class ModelService(using var game: GameInterface, var fileIO:FileIOInterface, var db:PersistenceInterface) {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val kafkaBootstrap =
  if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092" else "localhost:29092"

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrap)
    .withGroupId("model-command-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrap)

  val kafkaProducer = producerSettings.createKafkaProducer()

  Consumer.plainSource(consumerSettings, Subscriptions.topics("model-commands"))
    .mapAsync(1) { msg =>
      decode[ModelCommand](msg.value()) match {
        case Right(cmd) =>
          cmd.action match {
            case "createwinningboard" =>
              Future {
                game.createwinningboard()
              }
            case "setN" =>
              val versucheOpt = cmd.data.get("versuche").flatMap(_.asNumber.flatMap(_.toInt))
              versucheOpt match {
                case Some(versuche) => Future { game.setN(versuche) }
                case None => Future.failed(new RuntimeException("Versucheanzahl fehlt oder ist kein Int"))
              }
            case "createGameboard" =>
              Future{
                game.createGameboard()
              }
            case "changeState" =>
              val levelOpt = cmd.data.get("level").flatMap(_.asNumber.flatMap(_.toInt))
              levelOpt match {
                case Some(level) => Future {game.changeState(level)}
                case None => Future.failed(new RuntimeException("LevelNr. fehlt oder ist kein Int "))
              }
            case "save" =>
              Future{
                fileIO.save(game)
              }
            case "load" =>
              Future {
                val result: String = fileIO.load(game)
                val json = Json.obj(
                  "result" -> Json.fromString(result) // 👈 richtig!
                )
                val message = ResultEvent("load", json)
                val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                kafkaProducer.send(record) // wenn `producer` verfügbar ist
              }
            case "putGame" =>
              val nameOpt = cmd.data.get("name").flatMap(_.asString)
              nameOpt match {
                case Some(name) => Future{db.save(game, name)}
                case None => Future.failed(new RuntimeException("name fehlt oder ist kein String"))
              }
            case "getGame" =>
              val gameIdOpt = cmd.data.get("gameId").flatMap(_.asNumber.flatMap(_.toLong))
              gameIdOpt match{
                case Some(gameId) => Future{db.load(gameId, game)}
                case None => Future.failed(new RuntimeException("gameId fehlt oder ist kein Long"))
              }
            case "search" =>
              Future{
                val result = db.search()
                val json = Json.obj(
                  "result" -> Json.fromString(result)
                )
                val message = ResultEvent("search", json)
                val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                kafkaProducer.send(record)
              }
            case "step" =>
              val keyOpt = cmd.data.get("key").flatMap(_.asNumber.flatMap(_.toInt))
              val feedbackOpt = cmd.data.get("feedback").flatMap(_.asObject.map { obj =>
                obj.toMap.collect{
                  case (k, v) if v.isString =>
                    k.toInt -> v.asString.get
                }
              })
              keyOpt match {
                case Some(key) => Future{game.setRGameboard(key, feedbackOpt.get)}
                case None => Future.failed(new RuntimeException("Schluessel nicht dabei oder kein int"))
              }
            case "undoStep" =>
              val keyOpt = cmd.data.get("key").flatMap(_.asNumber.flatMap(_.toInt))
              val feedbackOpt = cmd.data.get("feedback").flatMap(_.asObject.map { obj =>
                obj.toMap.collect{
                  case (k, v) if v.isString =>
                    k.toInt -> v.asString.get
                }
              })
              keyOpt match {
                case Some(key) => Future{game.undoStep(key, feedbackOpt.get)}
                case None => Future.failed(new RuntimeException("Schluessel nicht dabei oder kein int"))
              }
            case "count" =>
              Future{
                val result = game.count()
                val json = Json.obj(
                  "continue" -> Json.fromInt(result)
                )
                val message = ResultEvent("count", json)
                val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                kafkaProducer.send(record)
              }
            case "getN" =>
              Future{
                val result = game.getN()
                val json = Json.obj(
                  "result" -> Json.fromInt(result)
                )
                val message = ResultEvent("getN", json)
                val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                kafkaProducer.send(record)
              }
            case "gamebaord" =>
              Future{
                val result = game.toString
                val json = Json.obj(
                  "gameboard" -> Json.fromString(result)
                )
                val message = ResultEvent("gamebaord", json)
                val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                kafkaProducer.send(record)
              }
            case "TargetwordToString" =>
              Future{
                val result = game.TargetwordToString()
                val json = Json.obj(
                  "targetWord" -> Json.fromString(result)
                )
                val message = ResultEvent("TargetwordToString", json)
                val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                kafkaProducer.send(record)
              }
            case "GuessTransform" =>
              val guessOpt = cmd.data.get("guess").flatMap(_.asString)
              guessOpt match{
                case Some(guess) =>
                  Future{
                    val result = game.GuessTransform(guess)
                    val json = Json.obj(
                      "transformedGuess" -> Json.fromString(result)
                    )
                    val message = ResultEvent("GuessTransform", json)
                    val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                    kafkaProducer.send(record)
                  }
                case None => Future.failed(new RuntimeException("Guess war nicht dabei oder kein String"))
              }
            case "controllLength" =>
              val lengthOpt = cmd.data.get("length").flatMap(_.asNumber.flatMap(_.toInt))
              lengthOpt match{
                case Some(length) =>
                  Future{
                    val result = game.controllLength(length)
                    val json = Json.obj(
                      "result" -> Json.fromBoolean(result)
                    )
                    val message = ResultEvent("controllLength", json)
                    val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                    kafkaProducer.send(record)
                  }
                case None => Future.failed(new RuntimeException("Guess war nicht dabei oder kein String"))
              }
            case "controllRealWord" =>
              val guessOpt = cmd.data.get("guess").flatMap(_.asNumber.flatMap(_.toInt))
              guessOpt match{
                case Some(guess) =>
                  Future{
                    val result = game.controllRealWord(guess)
                    val json = Json.obj(
                      "result" -> Json.fromBoolean(result)
                    )
                    val message = ResultEvent("controllRealWord", json)
                    val record = new ProducerRecord[String, String]("model-result", message.asJson.noSpaces)
                    kafkaProducer.send(record)
                  }
                case None => Future.failed(new RuntimeException("Guess war nicht dabei oder kein String"))
              }
            case "evaluateGuess" =>
              val evaluateGuessOpt = cmd.data.get("guess").flatMap(_.asString)
              evaluateGuessOpt match{
                case Some(evaluateGuess) =>
                  Future{
                    val result: Map[Int, String] = game.evaluateGuess(guess)

                    // Konvertiere Map[Int, String] zu Json
                    val jsonResult: Json = Json.obj(
                      "result" -> result.map { case (k, v) =>
                        k.toString -> Json.fromString(v)
                      }.asJson
                    )
                    val message = ResultEvent("evaluateGuess", jsonResult)

                    val record = new ProducerRecord[String, String](
                      "model-result",
                      message.asJson.noSpaces
                    )

                    kafkaProducer.send(record)
                  }
                case None => Future.failed(new RuntimeException("Guess war nicht dabei oder kein String"))
              }
            case other =>
              Future.failed(new RuntimeException(s"Unbekannter Aktion: $other"))
          }
        case Left(error) =>
          Future.failed(new RuntimeException(s"JSON Parsing Fehler: $error"))
      }
    }
    .runWith(Sink.ignore)
}
