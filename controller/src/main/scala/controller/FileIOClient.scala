package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.Materializer
import play.api.libs.json.Json

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration.*


class FileIOClient(baseurl:String)() {
  
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  
  def save():Unit ={
    val url = s"$baseurl/save"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }
  
  def load():String ={
    val url = s"$baseurl/load"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "result").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }
}
