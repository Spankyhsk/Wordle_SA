package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.stream.Materializer
//import play.api.libs.json.Json

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration.*

import io.circe.syntax.EncoderOps
import io.circe.Json
import org.apache.kafka.clients.producer.ProducerRecord
import io.circe.generic.auto._ // oder: import io.circe.generic.semiauto.deriveEncoder

class FileIOClient(alpakkaController: AlpakkaController)() {
  
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def save(): Unit = {
//    val url = s"$baseurl/save"
//    val request = HttpRequest(HttpMethods.POST, uri = url) // PUT für die "save"-Aktion
//    Await.result(Http().singleRequest(request), 5.seconds) // Warte auf die Antwort, aber ignoriere sie
    val command = ModelCommand("save", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def load(): String = {
//    val url = s"$baseurl/load"
//    val request = HttpRequest(HttpMethods.GET, uri = url) // GET für die "load"-Aktion
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//
//    // Verarbeite die Antwort und extrahiere den "result"-String
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "result").as[String] // Das "result"-Feld extrahieren und zurückgeben
    val command = ModelCommand("load", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("load") match {
      case Some(result) => result.data.get("result").flatMap(_.asString).getOrElse(
        throw new RuntimeException("Kein 'result'-Feld im Ergebnis gefunden")
      )
      case None => throw new RuntimeException("load aufruf hat nicht richtig geklappt")
    }
  }
}
