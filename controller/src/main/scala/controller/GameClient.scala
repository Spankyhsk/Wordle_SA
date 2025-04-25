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
    val url = s"$baseurl/game/count"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "continue").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def controllLength(n: Int): Boolean = {
    val url = s"$baseurl/game/controllLength/$n"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "result").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def controllRealWord(guess: String): Boolean = {
    val url = s"$baseurl/game/controllRealWord/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "result").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def evaluateGuess(guess: String): Map[Int, String] = {
    val url = s"$baseurl/game/evaluateGuess/$guess"
    // HTTP-Request an Microservice B
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Antwort verarbeiten
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    // JSON parsen und als Map[Int, String] zurückgeben
    val jsonResponse = Json.parse(entity.data.utf8String) // JSON parsen
    (jsonResponse.as[Map[Int, String]]) // Map extrahieren und zurückgeben
  }

  def guessTransform(guess: String): String = {
    val url = s"$baseurl/GuessTransform/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "transformedGuess").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def setVersuche(zahl: Integer): Unit = {
    val url = s"$baseurl/game/setN/$zahl"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def getVersuche(): Int = {
    val url = s"$baseurl/game/getN"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "result").as[Int] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def areYouWinningSon(guess: String): Boolean = {
    val url = s"$baseurl/game/areYouWinningSon/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "won").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def createWinningBoard(): Unit = {
    val url = s"$baseurl/game/createwinningboard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  //-----------------------------------------------------------------------------
  //               //board
  //-----------------------------------------------------------------------------

  
  def createGameboard(): Unit = {
    val url = s"$baseurl/game/createGameboard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def gameToString: String = {
    val url = s"$baseurl/game/toString"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "gameboard").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }

  //-----------------------------------------------------------------------------
  //               //Mode
  //-----------------------------------------------------------------------------
  
  def changeState(e: Int): Unit = {
    val url = s"$baseurl/game/changeState/$e"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Unit umgewandelt
  }

  def targetWordToString(): String = {
    val url = s"$baseurl/game/TargetwordToString"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "targetWord").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }

  //--------------------------------------------------------------------
  def step(key:Int, feedback:Map[Int,String]):Unit={
    val json: JsValue = Json.toJson(feedback)
    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$baseurl/game/step/$key",
      entity = entity
    )

    Http().singleRequest(request)
  }

  def undoStep(key:Int, feedback:Map[Int, String]):Unit={
    val json: JsValue = Json.toJson(feedback)
    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))

    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = s"$baseurl/game/undoStep/$key",
      entity = entity
    )

    Http().singleRequest(request)
  }

}