package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import play.api.libs.json.Json
import util.{Event, Observable}
import akka.stream.{ActorMaterializer, FlowShape, Materializer, UniformFanInShape, UniformFanOutShape}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, Source}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.NotUsed
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import io.circe.parser.*
import io.circe.Json

class UIApi()() extends Observable{

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val TUI = new TUI(new ControllerClient(sys.env.getOrElse("CONTROLLER_URL", "http://localhost:8081") + "/controller"))
  val GUISWING = new GUISWING(new ControllerClient(sys.env.getOrElse("CONTROLLER_URL", "http://localhost:8081") + "/controller"))
  add(TUI)
  add(GUISWING)

  val kafkaBootstrap = if (sys.env.get("RUNNING_IN_DOCKER").contains("true")) "kafka:9092" else "localhost:29092"

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrap)
    .withGroupId("ui-event-listener")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  Consumer
    .plainSource(consumerSettings, Subscriptions.topics("ui-events"))
    .map { msg =>
      val jsonString = msg.value()

      val eventOpt = for {
        json <- parse(jsonString).toOption
        eventStr <- json.hcursor.get[String]("eventType").toOption
        event <- Event.values.find(_.toString == eventStr)
      } yield event

      eventOpt match {
        case Some(event) =>
          println(s" UI-Event empfangen: $event")
          notifyObservers(event)
        case None =>
          println(s"⚠Konnte Event nicht erkennen in Nachricht: $jsonString")
      }
    }
    .runWith(Sink.ignore)

  private val controllerFlow: Flow[HttpRequest, String, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits.*

    val broadcast: UniformFanOutShape[HttpRequest, HttpRequest] = builder.add(Broadcast[HttpRequest](2))
    // Merge: Kombiniert die zwei Antwortpfade (GET und PUT) wieder zu einem gemeinsamen Stream
    val merge: UniformFanInShape[String, String] = builder.add(Merge[String](2))


    // GET-Verarbeitung
    val getFlow = Flow[HttpRequest].map {
      case req if req.uri.path.toString == "/ui/tui" =>
        HttpResponse(entity = "Willkommen zu Wordle\nBefehle\n$quit := Spiel beenden, $save := Speichern, $load := Laden, $switch := Schwierigkeiten verändern, $OnlineSave := Online Speichern")

      case req if req.uri.path.toString == "/ui/tui/getNewGame" =>
        HttpResponse(entity = Json.obj("newGame" -> TUI.getnewgame()).toString())

      case req if req.uri.path.toString == "/ui/tui/Select" =>
        HttpResponse(entity = "Gamemode aussuchen: \n1:= leicht\n2:= mittel\n3:= schwer")

      case _ =>
        HttpResponse(status = StatusCodes.NotFound, entity = "GET-Pfad nicht gefunden.")
    }

    val getResponseFlow = Flow[HttpResponse].mapAsync(1)(res => Unmarshal(res.entity).to[String])

    // PUT-/POST-Verarbeitung
    val putFlow = Flow[HttpRequest].mapAsync(1) {
      case req if req.uri.path.toString.startsWith("/ui/tui/processInput/") =>
        val input = req.uri.path.toString.split("/").last
        TUI.processInput(input)
        Future.successful(HttpResponse(entity = s"Input verarbeitet: $input"))

      case req if req.uri.path.toString.startsWith("/ui/tui/saveGame/") =>
        val name = req.uri.path.toString.split("/").last
        TUI.saveGame(name)
        Future.successful(HttpResponse(entity = "Spiel gespeichert"))
        
      case _ =>
        Future.successful(HttpResponse(status = StatusCodes.NotFound, entity = "PUT-/POST-Pfad nicht gefunden."))
    }

    val putResponseFlow = Flow[HttpResponse].mapAsync(1)(res => Unmarshal(res.entity).to[String])

    broadcast.out(0) ~> getFlow ~> getResponseFlow ~> merge.in(0)
    broadcast.out(1) ~> putFlow ~> putResponseFlow ~> merge.in(1)

    FlowShape(broadcast.in, merge.out)
  })


  val bindFuture = Http().newServerAt("0.0.0.0", 8080).bind(
    pathPrefix("ui") {
      extractRequest { request =>
        complete(
          Source.single(request)
            .via(controllerFlow)
            .runWith(Sink.head)
            .map(resp => resp)
        )
      }
    }
  )

  bindFuture.onComplete {
    case Success(binding) =>
      println(s"Controller Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Servers: $ex")
  }


  def valueOf(name: String): Event = name match {
    case "Move" => Event.Move
    case "NEW" => Event.NEW
    case "UNDO" => Event.UNDO
    case "LOSE" => Event.LOSE
    case "WIN" => Event.WIN
    case _ => throw new IllegalArgumentException(s"Unknown event: $name")
  }
}
