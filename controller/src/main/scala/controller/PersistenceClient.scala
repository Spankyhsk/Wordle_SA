package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord
import io.circe.syntax._
import io.circe.Json
import io.circe.generic.auto._

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class PersistenceClient(alpakkaController: AlpakkaController)() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def putGame(name: String): Unit = {
    val command = ModelCommand("putGame", Map("name" -> Json.fromString(name)))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("putGame") match {
      case Some(result) => result.data.get("continue").flatMap(_.asString)
      case None => throw new RuntimeException("putGame-Aufruf hat nicht richtig geklappt")
    }
  }

  def getGame(gameId: Long): Unit = {
    val command = ModelCommand("getGame", Map("gameId" -> Json.fromLong(gameId)))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("getGame") match {
      case Some(result) => result.data.get("continue").flatMap(_.asString)
      case None => throw new RuntimeException("getGame-Aufruf hat nicht richtig geklappt")
    }

  }

  def search(): String = {
    val command = ModelCommand("search", Map.empty)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("search") match {
      case Some(result) => result.data.get("continue").flatMap(_.asString).getOrElse("Kein Ergebnis")
      case None => throw new RuntimeException("search-Aufruf hat nicht richtig geklappt")
    }

  }
}
