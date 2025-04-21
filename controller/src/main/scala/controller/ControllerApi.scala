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
  controller.add(this)

  
  /**
   * Definiert die HTTP-Routen für die API.
   *
   * - `GET /contoller/count`: Gibt zurück, ob das Spiel fortgesetzt werden kann.
   *   Die Antwort ist ein JSON-Objekt mit einem `continue`-Feld, das einen Boolean-Wert enthält.
   */
  val route: Route =
    pathPrefix("contoller") {
      get {
        path("count") {
          val result = controller.count()
          complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("continue" -> result).toString()))
        }
      } ~
        path("getVersuche") {
          get {
            val result = controller.getVersuche()
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("versuche" -> result).toString()))
          }
        } ~
        path("getTargetword") {
          get {
            val result = controller.getTargetword()
            complete(StatusCodes.OK, HttpEntity(ContentTypes.`application/json`, Json.obj("targetword" -> result).toString()))
          }
        }
    }

  /**
   * Wird aufgerufen, wenn der Controller ein Event auslöst.
   * Aktuell ist diese Methode noch nicht implementiert.
   *
   * @param e Das ausgelöste Event.
   */
  override def update(e: Event): Unit = ???
}