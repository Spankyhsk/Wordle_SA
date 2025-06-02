package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.stream.Materializer
import play.api.libs.json.{Format, JsError, JsResult, JsValue, Json, OFormat}

import scala.concurrent.duration.*


import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class PersistenceClient(baseurl:String)() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def putGame(name: String): Unit = {
    val url = s"$baseurl/putGame/$name"
    val request = HttpRequest(HttpMethods.PUT, uri = url) // PUT für die "save"-Aktion
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort, aber ignoriere sie
  }


  def getGame(gameId: Long): Unit = {
    val url = s"$baseurl/getGame/$gameId"
    val request = HttpRequest(HttpMethods.GET, uri = url) // PUT für die "save"-Aktion
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort, aber ignoriere sie
  }

  def search(): String = {
    val url = s"$baseurl/search"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // sucht gameid und namen raus
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[String] 
  }

}