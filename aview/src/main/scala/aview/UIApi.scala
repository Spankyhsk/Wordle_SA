package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import play.api.libs.json.Json
import util.{Event, Observable}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}

class UIApi()() extends Observable{

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val TUI = new TUI(new ControllerClient(sys.env.getOrElse("CONTROLLER_URL", "http://localhost:8081") + "/controller"))
  val GUISWING = new GUISWING(new ControllerClient(sys.env.getOrElse("CONTROLLER_URL", "http://localhost:8081") + "/controller"))
  add(TUI)
  add(GUISWING)

  // Deine Route für den TUI-Endpunkt
  val routes: Route = {
    pathPrefix("ui") { // Der Prefix ist immer noch "ui"
      concat(
        // Route für den GET-Endpunkt "tui"
        path("tui") {
          get {
            complete("Willkommen zu Wordle\nBefehle\n$quit := Spiel beenden, $save := Speichern, $load := Laden, $switch := Schwierigkeiten verändern, $OnlineSave := Online Speichern")
          }
        },

        // Route für den PUT-Endpunkt "processInput"
        path("tui" / "processInput" / Segment) { input =>
          put {
            TUI.processInput(input)
            complete(StatusCodes.OK, s"Input verarbeitet: $input")
          }
        },

        // Route für den GET-Endpunkt "getNewGame"
        path("tui" / "getNewGame") {
          get {
            val result = TUI.getnewgame()
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("newGame" -> result).toString()))
          }
        },

        // Route für den GET-Endpunkt "Select"
        path("tui" / "Select") {
          get {
            complete("Gamemode aussuchen: \n1:= leicht\n2:= mittel\n3:= schwer")
          }
        },
        path("tui"/ "saveGame" / Segment){ name =>
          put{
            TUI.saveGame(name)
            complete(StatusCodes.OK)
          }
          
        },
        path("event") {
          post{
            entity(as[String]){ eventJson =>
              val eventString = (Json.parse(eventJson) \ "event").as[String]
              
              val event = valueOf(eventString)
              notifyObservers(event)
              complete(StatusCodes.OK)
            }
          }
        }
      )
    }
  }


  // Binde den Server an localhost:8080
  val bindFuture = Http().newServerAt("0.0.0.0", 8080).bind(routes)

  // Behandle das Future-Ergebnis von bind
  bindFuture.onComplete {
    case Success(binding) =>
      println(s"UI Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Servers: $ex")
  }

  def valueOf(name: String):Event= name match {
    case "Move" => Event.Move
    case "NEW" => Event.NEW
    case "UNDO" => Event.UNDO
    case "LOSE" => Event.LOSE
    case "WIN" => Event.WIN
    case _ => throw new IllegalArgumentException(s"Unknown event: $name")
  }
}

