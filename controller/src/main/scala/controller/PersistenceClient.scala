package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, StatusCodes}
import akka.stream.Materializer

import scala.concurrent.duration.*


import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class PersistenceClient(baseurl:String)() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  
  def putGame(name:String):Unit ={
    val url = s"$baseurl/putGame/$name"
    val request = HttpRequest(HttpMethods.PUT, uri = url) // PUT für die "save"-Aktion
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort, aber ignoriere sie
  }
}
