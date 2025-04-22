package controller

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Route
import model.gamefieldComponent.GamefieldInterface
import model.gamemodeComponnent.GamemodeInterface
import play.api.libs.json.Json
import util.{Event, Observer}


/**
 * Die Klasse `ControllerApi` stellt eine REST-API für den Controller bereit.
 * Sie ermöglicht es, über HTTP-Anfragen mit dem Controller zu interagieren.
 *
 * @param controller Die Instanz des Controllers, die die Spiellogik enthält.
 */
class ControllerApi(using var controller: ControllerInterface) extends Observer {
  // Registriert die API als Observer des Controllers
  controller.add(this) //unsicher ob wir die API als Observer brauchen

//Note: getTargetword -> getTargetwordString
  /**
   * Definiert die HTTP-Routen für die API.
   *
   * - `GET /contoller/count`: Gibt zurück, ob das Spiel fortgesetzt werden kann.
   * Die Antwort ist ein JSON-Objekt mit einem `continue`-Feld, das einen Boolean-Wert enthält.
   */
  val route: Route =
  pathPrefix("contoller") {
    concat(
      get {
        path("getCount") {
          val result = controller.count()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("continue" -> result).toString()))
        } ~
        path("getGuessTransform" / Segment) { guess =>
          val result = controller.GuessTransform(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("transformedGuess" -> result).toString()))
        } ~
        path("getControllLength" / IntNumber) { n =>
          val result = controller.controllLength(n)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        } ~
        path("getControllRealWord" / Segment) { guess =>
          val result = controller.controllRealWord(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("result" -> result).toString()))
        } ~
        path("getAreYouWinningSon" / Segment) { guess =>
          val result = controller.areYouWinningSon(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("won" -> result).toString()))
        } ~
        path("getVersuche") {
          val result = controller.getVersuche()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("versuche" -> result).toString()))
        } ~
        path("getEvaluateGuess" / Segment) { guess =>
          val result = controller.evaluateGuess(guess)
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.toJson(result).toString()))
        } ~
        path("getGameSave") {
          val result = controller.load()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("message" -> result).toString()))
        } ~
        path("getGameBoard") { 
          val result = controller.toString
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("gameboard" -> result).toString()))
        } ~
        path("getTargetWordString") {
          val result = controller.TargetwordToString()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("targetWord" -> result).toString()))
        }
      },
      post {
        path("postGameSave") {
          controller.save()
          complete(StatusCodes.OK, "Game saved")
        }
      },
      put {
        path("putMove" / IntNumber) { move =>
          entity(as[String]) { feedbackJson =>
            val feedback = Json.parse(feedbackJson).as[Map[Int, String]]
            controller.set(move, feedback)
            complete(StatusCodes.OK, "Move set")
          }
        } ~
        path("putCreateGameboard") {
          controller.createGameboard()
          complete(StatusCodes.OK, "Gameboard created")
        } ~
        path("putCreateWinningBoard") {
          controller.createwinningboard()
          complete(StatusCodes.OK, "Winning board created")
        } ~
        path("putUndoMove") {
          controller.undo()
          complete(StatusCodes.OK, "Move undone")
        }
      },
      patch {
        path("patchVersuche" / IntNumber) { versuche =>
          controller.setVersuche(versuche)
          complete(StatusCodes.OK, "Versuche set")
        } ~
        path("patchChangeState" / IntNumber) { state =>
          controller.changeState(state)
          complete(StatusCodes.OK, "State changed")
        }
      }
    )
  }

  /**
   * Wird aufgerufen, wenn der Controller ein Event auslöst.
   * Aktuell ist diese Methode noch nicht implementiert.
   *
   * @param e Das ausgelöste Event.
   */
  override def update(e: Event): Unit = ???
}