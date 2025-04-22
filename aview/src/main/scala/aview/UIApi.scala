package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class UIApi()(implicit system: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext) {

  val TUI = new TUI(new ControllerClient("http://localhost:8081"));
  val GUI = new GUISWING();

  // Deine Route für den TUI-Endpunkt
  val routes: Route = {
    path("tui") {
      get {
        complete("tui")
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

