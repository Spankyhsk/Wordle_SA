package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, StatusCodes}
import akka.stream.Materializer
import play.api.libs.json.Json
import util.Event

import scala.concurrent.{ExecutionContextExecutor, Future}

class ObserverClient(alpakkaController: AlpakkaController)() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def triggerEvent(event:Event): Future[Unit] = {
    alpakkaController.sendUiEvent(event)
    Future.successful(())
  }
}
