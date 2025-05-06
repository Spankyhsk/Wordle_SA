package model

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import play.api.libs.json.{JsObject, JsString, Json}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import model.Game
import model.*
import model.FileIOComponent.FileIOInterface
import model.persistenceComponent.PersistenceInterface
import model.persistenceComponent.slickComponent.SlickPersistenceImpl

class ModelApi(using var game: GameInterface, var fileIO:FileIOInterface, var db:PersistenceInterface){
  
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val slickPersistence = new SlickPersistenceImpl()

  val route: Route = pathPrefix("model") {
    concat(
      path("game" / "count") {
        get {
          val result = game.count()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("continue" -> result).toString()))
        }
      },
      path("game" / "controllLength" / IntNumber) { guess =>
        val result = game.controllLength(guess)
        complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
      },
      path("game" / "controllRealWord"/ Segment) { guess =>
        val result = game.controllRealWord(guess)
        val jsonResponse = Json.obj("result" -> result)
        complete(
          HttpResponse(
            StatusCodes.OK,
            entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(jsonResponse))
          )
        )
      },
      path("game" / "evaluateGuess"/ Segment) { guess =>
        val result = game.evaluateGuess(guess)
        val convertedMap = result.map{ case(k, v) => k.toString -> JsString(v) }
        val json = JsObject(convertedMap)
        complete(HttpEntity(ContentTypes.`application/json`, Json.stringify(json)))
      },
      path("game" / "areYouWinningSon" / Segment) { guess =>
        val result = game.areYouWinningSon(guess)
        val jsonResponse = Json.obj("won" -> result)
        complete(
          HttpResponse(
            StatusCodes.OK,
            entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(jsonResponse))
          )
        )
      },
      path("game" / "createwinningboard") {
        put {
          game.createwinningboard()
          complete(StatusCodes.OK)
        }
      },
      path("game" / "setN" / IntNumber) { versuche =>
        put {
            game.setN(versuche)
            complete(StatusCodes.OK)
          }
        },
      path("game" / "getN") {
        get {
          val result = game.getN();
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        }
      },
      path("game"/ "GuessTransform"/ Segment){ guess =>
        get {
          val result = game.GuessTransform(guess)
          val jsonResponse = Json.obj("transformedGuess" -> result)
          complete(
            HttpResponse(
              StatusCodes.OK,
              entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(jsonResponse))
            )
          )
        }
      },
      path("game" / "createGameboard") {
        put {
          game.createGameboard()
          complete(StatusCodes.OK)
        }
      },
      path("game" / "toString"){
        get {
          val result = game.toString
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("gameboard" -> result).toString()))
        }
      },
      path("game" / "changeState" / IntNumber){ level =>
        patch{
          game.changeState(level)
          complete(StatusCodes.OK)
        }
      },
      path("game" / "TargetwordToString"){
        get {
          val result = game.TargetwordToString()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("targetWord" -> result).toString()))
        }
      },
      path("fileIO" / "save") {
      post {
        fileIO.save(game)
        complete {
          "Spiel wurde gespeichert."
        }
      }
    },
    path("fileIO" / "load") {
      get {
        val result = fileIO.load(game)
        complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
      }
    },
    path("dao" / "loadDB") { gameId =>
      get {
        val result = slickPersistence.load(gameId, game)
        complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
      }
    },
    path("dao" / "saveDB") { name =>
      post {
        slickPersistence.save(game, name)
        complete {
          "Spiel wurde gespeichert."
        }
      }
    },
    path("dao" / "search") {
      get {
        val result = slickPersistence.search()
        complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
      }
    },
      path("game"/"step"/IntNumber) { key =>
        post {
          entity(as[String]) { body =>
            val json = Json.parse(body)
            val receivedMap = json.as[Map[Int, String]]
            game.setRGameboard(key, receivedMap)
            complete(StatusCodes.OK)
          }
        }
      },
      path("game"/"undoStep"/IntNumber) { key =>
        post {
          entity(as[String]) { body =>
            val json = Json.parse(body)
            val receivedMap = json.as[Map[Int, String]]
            game.undoStep(key, receivedMap)
            complete(StatusCodes.OK)
          }
        }
      },
      path("persistence"/"putGame"/ Segment){ name =>
        put{
          db.save(game, name)
          complete(StatusCodes.OK)
        }
      }
    )}

  val bindFuture = Http().newServerAt("0.0.0.0", 8082).bind(route)
  // Behandle das Future-Ergebnis von bind
  bindFuture.onComplete {
    case Success(binding) =>
      println(s"Model Server läuft auf ${binding.localAddress}")
    case Failure(ex) =>
      println(s"Fehler beim Starten des Servers: $ex")
  }
}
