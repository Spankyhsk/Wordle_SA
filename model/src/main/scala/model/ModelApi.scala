package model

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._
import scala.io.StdIn


import model._

class ModelApi(using var game: GameInterface){
  implicit val system = ActorSystem("wordle-api")
  implicit val materializer = Materializer(system)
  implicit val executionContext = system.dispatcher

  val route: Route = pathPrefix("model") {
    concat(
      path("game" / "count") {
        get {
          complete(game.count())
        }
      },
      path("game" / "controllLength") {
        parameter("n".as[Int]) { n =>
          complete(game.controllLength(n))
        }
      },
      path("game" / "controllRealWord") {
        parameter("guess") { guess =>
          complete(game.controllRealWord(guess))
        }
      },
      path("game" / "evaluateGuess") {
        parameter("guess") { guess =>
          complete(game.evaluateGuess(guess))
        }
      },
      path("game" / "areYouWinningSon") {
        parameter("guess") { guess =>
          complete(game.areYouWinningSon(guess))
        }
      },
      path("game" / "createwinningboard") {
        put {
          game.createwinningboard()
          complete(StatusCodes.OK)
        }
      },
      path("game" / "setWinningboard") {
        post {
          entity(as[WinningBoardPayload]) { payload =>
            game.setWinningboard(payload.wBoard)
            complete(StatusCodes.OK)
          }
        }
      },
      path("game" / "setN") {
        post {
          entity(as[SetNPayload]) { payload =>
            game.setN(payload.n)
            complete(StatusCodes.OK)
          }
        }
      },
      path("game" / "getN") {
        get {
          complete(game.getN())
        }
      },
      path("game" / "resetGameboard") {
        put {
          game.resetGameboard()
          complete(StatusCodes.OK)
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
        complete {
          fileIO.save(game)
          "Spiel wurde gespeichert."
        }
      }
    },
    path("fileIO" / "load") {
      post {
        complete {
          val result = fileIO.load(game)
          result
        }
      }
    }
    )
  }

  val bindingFuture = Http().newServerAt("localhost", 8082).bind(route)
  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
