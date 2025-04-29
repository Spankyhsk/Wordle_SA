package rest

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.stream.Materializer
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

// Beispiel-Datenmodell
case class GameStatus(status: String)
object GameStatus {
  implicit val format: OFormat[GameStatus] = Json.format[GameStatus]
}

object RestService {
  implicit val system: ActorSystem = ActorSystem("rest-service")
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    // Routen definieren
    // todo: Dummy-Routen für den REST-Service. Ist halt die Frage was man hier für Routen am Ende verwenden soll (Also Tui-, Gui-API calls oder direkt in Controller oder sogar ins Model).
    val route =
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

    // Server starten
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)
    println("REST-Service läuft unter http://localhost:8080/")
    StdIn.readLine() // Server läuft, bis Enter gedrückt wird
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}