package controller

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, FlowShape, Materializer, UniformFanInShape, UniformFanOutShape}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.GraphDSL.Builder
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, Source}
import play.api.libs.json.Json
import util.{Event, Observer}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContextExecutor, Future}


/**
 * Die Klasse `ControllerApi` stellt eine REST-API für den Controller bereit.
 * Sie ermöglicht es, über HTTP-Anfragen mit dem Controller zu interagieren.
 *
 * @param controller Die Instanz des Controllers, die die Spiellogik enthält.
 */
class ControllerApi(using var controller: ControllerInterface) extends Observer {
  // Registriert die API als Observer des Controllers

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
//Note: getTargetword -> getTargetwordString
  
  // controllerFlow: verarbeitet HttpRequest → gibt String zurück
  private val controllerFlow: Flow[HttpRequest, String, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder: Builder[NotUsed] =>
    import GraphDSL.Implicits._

    // Broadcast: Teilt jeden eingehenden HttpRequest in zwei Pfade auf (z.B. für GET & PUT getrennt behandeln)
    val broadcast:UniformFanOutShape[HttpRequest, HttpRequest] = builder.add(Broadcast[HttpRequest](2))
    // Merge: Kombiniert die zwei Antwortpfade (GET und PUT) wieder zu einem gemeinsamen Stream
    val merge: UniformFanInShape[String, String] = builder.add(Merge[String](2))

    // GET-Anfragen behandeln (einfache Pfade → Antwort als HttpResponse generieren)
    val getControllerFlow = Flow[HttpRequest].map { request =>
      request.uri.path.toString match {
        // Pfad-Matching: entscheidet anhand der URI, was im Controller gemacht wird
        case "/controller/getCount" =>
          HttpResponse(entity = Json.obj("continue" -> controller.count()).toString)
        case "/controller/getVersuche" =>
          HttpResponse(entity = Json.obj("versuche" -> controller.getVersuche()).toString)
        case "/controller/getGameSave" =>
          HttpResponse(entity = Json.obj("message" -> controller.load()).toString)
        case "/controller/search" =>
          HttpResponse(entity = Json.obj("message" -> controller.search()).toString)
        case "/controller/getGameBoard" =>
          HttpResponse(entity = Json.obj("gameboard" -> controller.toString).toString)
        case "/controller/getTargetWordString" =>
          HttpResponse(entity = Json.obj("targetWord" -> controller.TargetwordToString()).toString)
        case path if path.startsWith("/controller/getGuessTransform") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          println(s"Received guess in controllerAPI: $guess")
          HttpResponse(entity = Json.obj("transformedGuess" -> controller.GuessTransform(guess)).toString)
        case path if path.startsWith("/controller/getControllLength") =>
          val query = request.uri.query()
          val length = query.get("length").get.toInt
          HttpResponse(entity = Json.obj("result" -> controller.controllLength(length)).toString)
        case path if path.startsWith("/controller/getControllRealWord") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          HttpResponse(entity = Json.obj("result" -> controller.controllRealWord(guess)).toString)
        case path if path.startsWith("/controller/getAreYouWinningSon") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          HttpResponse(entity = Json.obj("won" -> controller.areYouWinningSon(guess)).toString)
        case path if path.startsWith("/controller/getEvaluateGuess") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          HttpResponse(entity = Json.toJson(controller.evaluateGuess(guess)).toString)
      }
    }
    
    // Wandelt HttpResponse (der GET-Antwort) in einen String um
    val getResponseFlow = Flow[HttpResponse].mapAsync(1) { response =>
      Unmarshal(response.entity).to[String]
    }

    // PUT-/POST-artige Requests (asynchrone Verarbeitung, z.B. JSON-Bodies lesen)
    val putControllerFlow = Flow[HttpRequest].mapAsync(1) { request =>
      request.uri.path.toString match {
        case "/controller/postGameSave" =>
          controller.save()
          Future.successful(HttpResponse(entity = "Game saved"))
        case "/controller/putCreateGameboard" =>
          controller.createGameboard()
          Future.successful(HttpResponse(entity= "Gameboard created"))
        case "/controller/putCreateWinningBoard" =>
          controller.createwinningboard()
          Future.successful(HttpResponse(entity = "Winning board created"))
        case "/controller/putUndoMove" =>
          controller.undo()
          Future.successful(HttpResponse(entity = "Move undone"))
        case "/controller/startGame" =>
          controller.startGame()
          Future.successful(HttpResponse(entity = "Spiel gestartet"))
        case path if path.startsWith("/controller/putVersuche") =>
          val query = request.uri.query()
          val versuche = query.get("versuche").get.toInt
          controller.setVersuche(versuche)
          Future.successful(HttpResponse(entity = "Versuche set"))
        case path if path.startsWith("/controller/getGame") =>
          val query = request.uri.query()
          val gameId = query.get("gameId").get.toLong
          controller.getGame(gameId)
          Future.successful(HttpResponse(entity = "getGame"))
        case path if path.startsWith("/controller/putGame") =>
          val query = request.uri.query()
          val name = query.get("name").get
          controller.putGame(name)
          Future.successful(HttpResponse(entity = "putGame"))
        case path if path.startsWith("/controller/patchChangeState") =>
          val query = request.uri.query()
          val state = query.get("state").get.toInt
          controller.changeState(state)
          Future.successful(HttpResponse(entity = "State changed"))
        case path if path.startsWith("/controller/putMove") =>
          val query = request.uri.query()
          val move = query.get("move").get.toInt
          request.entity.toStrict(Duration.apply(3, TimeUnit.SECONDS)).map { entity =>
            val requestbody = entity.data.utf8String
            controller.set(move, Json.parse(requestbody).as[Map[Int, String]])
            HttpResponse(entity = "Move set")
          }
      }
    }


    // Wie bei GET: extrahiere String-Body aus HttpResponse
    val putResponseFlow = Flow[HttpResponse].mapAsync(1) { response =>
      Unmarshal(response.entity).to[String]
    }


    // Die Pfade zusammenbauen: beide Pfade gehen von broadcast → zu merge

    // GET-Pfad: erster Ausgang → getControllerFlow → getResponseFlow → merge.in(0)
    broadcast.out(0) ~> getControllerFlow ~> getResponseFlow ~> merge.in(0)

    // PUT-Pfad: zweiter Ausgang → putControllerFlow → putResponseFlow → merge.in(1)
    broadcast.out(1) ~> putControllerFlow ~> putResponseFlow ~> merge.in(1)

    // Ergebnis-Flow (von broadcast bis merge)
    FlowShape(broadcast.in, merge.out)

  })

  //AkkaKafkaControllerConsumer.consume()
  
  /**
   * Wird aufgerufen, wenn der Controller ein Event auslöst.
   * Aktuell ist diese Methode noch nicht implementiert.
   *
   * @param e Das ausgelöste Event.
   */
  override def update(e: Event): Unit = ???

  // Binde den Server an localhost:8080
  val bindFuture = Http().newServerAt("0.0.0.0", 8081).bind(
    pathPrefix("controller") {
      extractRequest { request =>
        complete(
          Source.single(request).via(controllerFlow).runWith(Sink.head).map(resp => resp)
        )
      }
    }
  )

  // Behandle das Future-Ergebnis von bind
  bindFuture.onComplete {
    case Success(binding) =>
      println(s"Controller Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Servers: $ex")
  }
}