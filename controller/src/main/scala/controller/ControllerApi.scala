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
        path("count") {
          val result = controller.count()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("continue" -> result).toString()))
        } ~
        path("getVersuche") {
          val result = controller.getVersuche()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("versuche" -> result).toString()))
        } ~
        path("getTargetword") {
          val result = controller.getTargetword()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.toJson(result).toString()))
        } ~
        path("toString") {
          val result = controller.toString
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`text/plain(UTF-8)`, result))
        }
      },
      post {
        path("evaluateGuess") {
          entity(as[String]) { guess =>
            val result = controller.evaluateGuess(guess)
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.toJson(result).toString()))
          }
        } ~
        path("areYouWinningSon") {
          entity(as[String]) { guess =>
            val result = controller.areYouWinningSon(guess)
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("won" -> result).toString()))
          }
        } ~
        path("changeState" / IntNumber) { state =>
          controller.changeState(state)
          complete(StatusCodes.OK, "State changed")
        } ~
        path("createGameboard") {
          controller.createGameboard()
          complete(StatusCodes.OK, "Gameboard created")
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