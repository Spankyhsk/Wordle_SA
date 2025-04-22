package de.niklas.wordle.modelApi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json._

import scala.io.StdIn

import de.niklas.wordle.model.Game

// Einfaches JSON-Format
case class GameState(grid: Vector[String], guesses: Int, status: String)

trait JsonSupport extends DefaultJsonProtocol {
  implicit val gameStateFormat = jsonFormat3(GameState)
  implicit val guessFormat = jsonFormat1(Guess)
}
case class Guess(word: String)

object ModelApi extends App with JsonSupport {

  implicit val system = ActorSystem("wordle-api")
  implicit val materializer = Materializer(system)
  implicit val executionContext = system.dispatcher

  val game = Game() // Instanz aus deinem model-Modul

  val route =
    path("game") {
      get {
        val state = GameState(
          grid = game.grid,                      // z. B. ["APPLE", "_____"]
          guesses = game.numberOfGuesses,
          status = game.status.toString         // "Running", "Won", "Lost"
        )
        complete(state)
      }
    } ~
    path("guess") {
      post {
        entity(as[Guess]) { guess =>
          game.guess(guess.word) // deine Game-Logik
          complete("OK")
        }
      }
    } ~
    path("status") {
      get {
        complete(game.status.toString)
      }
    }

  val bindingFuture = Http().newServerAt("localhost", 8081).bind(route)
  println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
