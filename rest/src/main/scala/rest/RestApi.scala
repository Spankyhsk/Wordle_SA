package rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}


class RestApi(){
  implicit val system: ActorSystem = ActorSystem("rest-service")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Routen definieren
  // todo: Dummy-Routen für den REST-Service. Ist halt die Frage was man hier für Routen am Ende verwenden soll (Also Tui-, Gui-API calls oder direkt in Controller oder sogar ins Model).
  val route: Route =
    path("status") {
      get {
        complete(Json.obj("status" -> "Game is running").toString())
      }
    } ~
      path("move") {
        post {
          entity(as[String]) { move =>
            println(s"Received move: $move")
            complete(Json.obj("message" -> s"Move '$move' received").toString())
          }
        }
      }

  val bindFuture = Http().newServerAt("0.0.0.0", 9000).bind(route)

  // Behandle das Future-Ergebnis von bind
  bindFuture.onComplete {
    case Success(binding) =>
      println(s"Rest Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Rest Servers: $ex")
  }
}
