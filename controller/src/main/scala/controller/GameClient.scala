package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity
//import akka.http.impl.util.JavaMapping.HttpEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import model.GameInterface
import model.gamefieldComponent.{GamefieldInterface, gameboard}
import model.gamemechComponent.gamemechInterface
import play.api.libs.json.{Format, JsError, JsResult, JsValue, Json, OFormat}

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}


class GameClient(baseurl:String)() {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val gamefieldFormat: Format[GamefieldInterface[GamefieldInterface[String]]] = new Format[GamefieldInterface[GamefieldInterface[String]]] {
    override def reads(json: JsValue): JsResult[GamefieldInterface[GamefieldInterface[String]]] = {
      json.validate[Map[Int, Map[Int, String]]].map { boardMap =>
        GamefieldFactory.createGameboard().setMap(boardMap)
      }
    }

    override def writes(o: GamefieldInterface[GamefieldInterface[String]]): JsValue = {
      val boardMap = o.getMap().map { case (key, gameField) =>
        key -> gameField.getMap()
      }
      Json.toJson(boardMap)
    }
  }

  private object GamefieldFactory {
    def createGameboard(): GamefieldInterface[GamefieldInterface[String]] = new gameboard()
  }


  def count(): Boolean = {
    val url = s"$baseurl/count"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "continue").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def controllLength(n: Int): Boolean = {
    val url = s"$baseurl/controllLength/$n"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "result"-Boolean
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Boolean] // Das "result"-Feld extrahieren und zurückgeben
  }

  def controllRealWord(guess: String): Boolean = {
    val url = s"$baseurl/controllRealWord/$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "result"-Boolean
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Boolean] // Das "result"-Feld extrahieren und zurückgeben
  }

  def evaluateGuess(guess: String): Map[Int, String] = {
    val url = s"$baseurl/evaluateGuess/$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Antwort verarbeiten
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    // JSON parsen und als Map[Int, String] zurückgeben
    val jsonResponse = Json.parse(entity.data.utf8String)
    jsonResponse.as[Map[Int, String]]
  }

  def guessTransform(guess: String): String = {
    val url = s"$baseurl/GuessTransform/$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "transformedGuess"-String
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "transformedGuess").as[String] // Das "transformedGuess"-Feld extrahieren und zurückgeben
  }

  def setVersuche(zahl: Integer): Unit = {
    val url = s"$baseurl/setN/$zahl"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
  }

  def getVersuche(): Int = {
    val url = s"$baseurl/getN"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "result"-Integer
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Int] // Das "result"-Feld extrahieren und zurückgeben
  }

  def areYouWinningSon(guess: String): Boolean = {
    val url = s"$baseurl/areYouWinningSon/$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "won"-Boolean
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "won").as[Boolean] // Das "won"-Feld extrahieren und zurückgeben
  }

  def createWinningBoard(): Unit = {
    val url = s"$baseurl/createwinningboard"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
  }

  def createGameboard(): Unit = {
    val url = s"$baseurl/createGameboard"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
  }

  def gameToString: String = {
    val url = s"$baseurl/toString"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "gameboard"-String
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "gameboard").as[String] // Das "gameboard"-Feld extrahieren und zurückgeben
  }

  def changeState(e: Int): Unit = {
    val url = s"$baseurl/changeState/$e"
    val request = HttpRequest(HttpMethods.PATCH, uri = url)
    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
  }

  def targetWordToString(): String = {
    val url = s"$baseurl/TargetwordToString"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    // Verarbeite die Antwort und extrahiere den "targetWord"-String
    val entityFuture = response.entity.toStrict(30.seconds)
    val entity = Await.result(entityFuture, 30.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "targetWord").as[String] // Das "targetWord"-Feld extrahieren und zurückgeben
  }


  //--------------------------------------------------------------------
  def step(key:Int, feedback:Map[Int,String]):Unit={
    val json: JsValue = Json.toJson(feedback)
    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$baseurl/step/$key",
      entity = entity
    )

    Http().singleRequest(request)
  }

  def undoStep(key:Int, feedback:Map[Int, String]):Unit={
    val json: JsValue = Json.toJson(feedback)
    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$baseurl/undoStep/$key",
      entity = entity
    )

    Http().singleRequest(request)
  }

}