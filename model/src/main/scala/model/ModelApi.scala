package model

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, FlowShape, Materializer, UniformFanInShape, UniformFanOutShape}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import play.api.libs.json.{JsObject, JsString, Json}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import model.Game
import model.*
import model.FileIOComponent.FileIOInterface
import model.persistenceComponent.PersistenceInterface
import model.persistenceComponent.slickComponent.SlickPersistenceImpl

import akka.NotUsed
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.GraphDSL.Builder
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Sink, Source}

import scala.concurrent.Future

class ModelApi(using var game: GameInterface, var fileIO:FileIOInterface, var db:PersistenceInterface){
  
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // modelFlow: verarbeitet HttpRequest → gibt String zurück
  private val modelFlow: Flow[HttpRequest, String, NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder: Builder[NotUsed] =>
    import GraphDSL.Implicits._

    // Broadcast: Teilt jeden eingehenden HttpRequest in zwei Pfade auf (z.B. für GET & PUT getrennt behandeln)
    val broadcast: UniformFanOutShape[HttpRequest, HttpRequest] = builder.add(Broadcast[HttpRequest](2))
    // Merge: Kombiniert die zwei Antwortpfade (GET und PUT) wieder zu einem gemeinsamen Stream
    val merge: UniformFanInShape[String, String] = builder.add(Merge[String](2))

    // GET-Anfragen behandeln (einfache Pfade → Antwort als HttpResponse generieren)
    val getModelFlow = Flow[HttpRequest].map { request =>
      request.uri.path.toString match {
        // Pfad-Matching: entscheidet anhand der URI, was im Model gemacht wird
        case "/model/game/count" =>
          HttpResponse(entity = Json.obj("continue" -> game.count()).toString)
        case "/model/game/getN" =>
          HttpResponse(entity = Json.obj("result" -> game.getN()).toString)
        case "/model/game/toString" =>
          HttpResponse(entity = Json.obj("gameboard" -> game.toString).toString)
        case "/model/game/TargetwordToString" =>
          HttpResponse(entity = Json.obj("targetWord" -> game.TargetwordToString()).toString)
        case path if path.startsWith("/model/game/GuessTransform") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          HttpResponse(entity = Json.obj("transformedGuess" -> game.GuessTransform(guess)).toString)
        case path if path.startsWith("/model/game/controllLength") =>
          val query = request.uri.query()
          val length = query.get("length").get.toInt
          HttpResponse(entity = Json.obj("result" -> game.controllLength(length)).toString)
        case path if path.startsWith("/model/game/controllRealWord") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          HttpResponse(entity = Json.obj("result" -> game.controllRealWord(guess)).toString)
        case path if path.startsWith("/model/game/areYouWinningSon") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          HttpResponse(entity = Json.obj("won" -> game.areYouWinningSon(guess)).toString)
        case path if path.startsWith("/model/game/evaluateGuess") =>
          val query = request.uri.query()
          val guess = query.get("guess").get
          val result = game.evaluateGuess(guess)
          val convertedMap = result.map { case (k, v) => k.toString -> Json.toJsFieldJsValueWrapper(JsString(v)) }
          HttpResponse(entity = Json.obj(convertedMap.toSeq: _*).toString)
        case other =>
          HttpResponse(status = StatusCodes.NotFound, entity = s"Pfad nicht gefunden: $other")
      }
    }

    // Wandelt HttpResponse (der GET-Antwort) in einen String um
    val getResponseFlow = Flow[HttpResponse].mapAsync(1) { response =>
      Unmarshal(response.entity).to[String]
    }

    // PUT-/POST-artige Requests (asynchrone Verarbeitung, z.B. JSON-Bodies lesen)
    val putModelFlow = Flow[HttpRequest].mapAsync(1) { request =>
      request.uri.path.toString match {
        case "/model/game/createwinningboard" =>
          game.createwinningboard()
          Future.successful(HttpResponse(entity = "Winningboard wurde erstellt."))
        case path if path.startsWith("/model/game/setN") =>
          val query = request.uri.query()
          val versuche = query.get("versuche").get.toInt
          game.setN(versuche)
          Future.successful(HttpResponse(entity = "Anzahl versuche wurde gesetzt."))
        case "/model/game/createGameboard" =>
          game.createGameboard()
          Future.successful(HttpResponse(entity = "Spielbrett wurde erstellt."))
        case path if path.startsWith("/model/game/changeState") =>
          val query = request.uri.query()
          val level = query.get("level").get.toInt
          game.changeState(level)
          Future.successful(HttpResponse(entity = "Schwirigkeit/Status wurde geändert."))
        case "/model/fileIO/save" =>
          fileIO.save(game)
          Future.successful(HttpResponse(entity = "Spiel wurde gespeichert."))
        case "/model/fileIO/load" =>
          val result = fileIO.load(game)
          Future.successful(HttpResponse(entity = Json.obj("result" -> result).toString))
        case path if path.startsWith("/model/persistence/putGame") =>
          val query = request.uri.query()
          val name = query.get("name").get
          db.save(game, name)
          Future.successful(HttpResponse(entity = "Spiel wurde gespeichert."))
        case path if path.startsWith("/model/persistence/getGame") =>
          val query = request.uri.query()
          val gameId = query.get("gameId").get.toLong
          db.load(gameId, game)
          Future.successful(HttpResponse(entity = Json.obj("message" -> "Spiel wurde geladen").toString))
        case "/model/persistence/search" =>
          val result = db.search()
          Future.successful(HttpResponse(entity = Json.obj("result" -> result).toString))
        case path if path.startsWith("/model/game/step") =>
          val query = request.uri.query()
          val key = query.get("key").get.toInt
          Unmarshal(request.entity).to[String].map { body =>
            val json = Json.parse(body)
            val receivedMap = json.as[Map[Int, String]]
            game.setRGameboard(key, receivedMap)
            HttpResponse(StatusCodes.OK)
          }
        case path if path.startsWith("/model/game/undoStep") =>
          val query = request.uri.query()
          val key = query.get("key").get.toInt
          Unmarshal(request.entity).to[String].map { body =>
            val json = Json.parse(body)
            val receivedMap = json.as[Map[Int, String]]
            game.undoStep(key, receivedMap)
            HttpResponse(StatusCodes.OK)
          }
        case other =>
          Future.successful(HttpResponse(status = StatusCodes.NotFound, entity = s"Pfad nicht gefunden: $other"))
      }
    }

    // Wie bei GET: extrahiere String-Body aus HttpResponse
    val putResponseFlow = Flow[HttpResponse].mapAsync(1) { response =>
      Unmarshal(response.entity).to[String]
    }

    // Verbinde die Komponenten
    // GET-Pfad: erster Ausgang -> getModelFlow → getResponseFlow → merge.in(0)
    broadcast.out(0) ~> getModelFlow ~> getResponseFlow ~> merge.in(0)

    // PUT-Pfad: zweiter Ausgang -> putModelFlow → putResponseFlow → merge.in(1)
    broadcast.out(1) ~> putModelFlow ~> putResponseFlow ~> merge.in(1)

    // Rückgabe des Flows
    FlowShape(broadcast.in, merge.out)
  })

  val bindFuture = Http().newServerAt("0.0.0.0", 8082).bind(
    pathPrefix("model") {
      extractRequest { request =>
        complete(
          Source.single(request).via(modelFlow).runWith(Sink.head).map(resp => resp)
        )
      }
    }
  )

  // Behandle das Future-Ergebnis von bind
  bindFuture.onComplete {
    case Success(binding) =>
      println(s"Model Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Servers: $ex")
  }
}
