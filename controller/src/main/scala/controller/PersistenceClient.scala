package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord
import io.circe.syntax.*
import io.circe.Json
import io.circe.generic.auto.*

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future, Promise}

class PersistenceClient(alpakkaController: AlpakkaController)() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def putGame(name: String): Unit = {
    val command = ModelCommand("putGame", Map("name" -> Json.fromString(name)))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def getGame(gameId: Long): Unit = {
    val command = ModelCommand("getGame", Map("gameId" -> Json.fromLong(gameId)))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

  }

  def search(): String = {
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("search", promise)

    val command = ModelCommand("search", Map.empty)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
    
    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("search").flatMap(_.asString).getOrElse(
      throw new RuntimeException("Kein 'result'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von search: $resultData")
    resultData
  }
}
