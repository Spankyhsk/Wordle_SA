package model

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.{ActorMaterializer, Materializer}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.*
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success}
import model.Game
import model.*
import model.FileIOComponent.FileIOInterface

class ModelApi(using var game: GameInterface, var fileIO:FileIOInterface){
  
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val route: Route = pathPrefix("model") {
    concat(
      path("game" / "count") {
        get {
          val result = game.count()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("continue" -> result).toString()))
        }
      },
      path("game" / "controllLength") {
        parameter("n".as[Int]) { n =>
          val result = game.controllLength(n)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        }
      },
      path("game" / "controllRealWord") {
        parameter("guess") { guess =>
          val result = game.controllRealWord(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        }
      },
      path("game" / "evaluateGuess") {
        parameter("guess") { guess =>
          val result = game.evaluateGuess(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        }
      },
      path("game" / "areYouWinningSon") {
        parameter("guess") { guess =>
          val result = game.areYouWinningSon(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        }
      },
      path("game" / "createwinningboard") {
        put {
          game.createwinningboard()
          complete(StatusCodes.OK)
        }
      },
      path("game" / "setN" / IntNumber) { versuche =>
        post {
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
      path("game" / "createGameboard") {
        put {
          game.createGameboard()
          complete(StatusCodes.OK)
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
      post {
        val result = fileIO.load(game)
        complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
      }
    }
    )
  }

  val bindingFuture = Http().newServerAt("localhost", 8083).bind(route)
  println(s"Server online at http://localhost:8083/\nPress RETURN to stop...")
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
