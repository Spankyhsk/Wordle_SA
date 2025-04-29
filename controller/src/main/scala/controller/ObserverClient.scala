package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.POST
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, StatusCodes}
import akka.stream.Materializer
import play.api.libs.json.Json
import util.Event

import scala.concurrent.{ExecutionContextExecutor, Future}

class ObserverClient(baseurl:String)() {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def triggerEvent(event:Event): Future[Unit] = {
    // Controller löst ein Ereignis aus
    //println("Controller hat ein Event ausgelöst!")

    // Event an UI API senden (z.B. "EVENT_TRIGGERED")
    val eventJson = s"""{"event": "${event.toString}"}"""

    val request = HttpRequest(
      method = POST,
      uri = s"$baseurl/event",
      entity = HttpEntity(ContentTypes.`application/json`, eventJson)
    )

    // Sende den Request
    Http().singleRequest(request).map {
      case response if response.status == StatusCodes.OK =>
        //println(s"Event ${event.toString} erfolgreich an die UI gesendet.")
      case _ =>
        println(s"Fehler beim Senden des Events ${event.toString} an die UI.")
    }
  }
}
