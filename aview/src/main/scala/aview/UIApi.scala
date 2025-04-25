package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

class UIApi()() {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val TUI = new TUI(new ControllerClient("http://localhost:8081"))
  val GUISWING = new GUISWING(new ControllerClient("http://localhost:8081"))

  // Deine Route für den TUI-Endpunkt
  val routes: Route = {
    path("tui") {
      get {
        println("tui wurde UFGERUFEN")
        complete("Willkommen zu Wordle\nBefehle\n$quit := Spiel beenden, $save := Speichern, $load := Laden, $switch := Schwierigkeiten verändern")
      }
    }~
    path("tui"/ "processInput"/Segment){ input =>
      put{
        TUI.processInput(input)
        complete(StatusCodes.OK)
      }
    }
    path("tui"/ "getNewGame"){
      get{
        val result = TUI.getnewgame()
        complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("newGame" -> result).toString()))
      }
    }
    path("tui"/"Select"){
      complete("Gamemode aussuchen: \n1:= leicht\n2:= mittel\n3:= schwer")
    }
  }

  // Binde den Server an localhost:8080
  val bindFuture = Http().newServerAt("localhost", 8080).bind(routes)

  // Behandle das Future-Ergebnis von bind
  bindFuture.onComplete {
    case Success(binding) =>
      println(s"Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Servers: $ex")
  }
}

