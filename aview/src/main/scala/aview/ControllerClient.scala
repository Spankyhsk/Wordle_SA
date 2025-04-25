package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import play.api.libs.json.*

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.duration.*


class ControllerClient(baseurl:String)() {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  
  def getCount():Boolean={
    val url = s"$baseurl/contoller/getCount"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "continue").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getGuessTransform(input:String):String={
    val url = s"$baseurl/contoller/getGuessTransform"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "transformedGuess").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getControllLength(length:Int):Boolean={
    val url = s"$baseurl/contoller/getControllLength/$length"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "result").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getControllRealWord(guess:String):Boolean={
    val url = s"$baseurl/contoller/getControllRealWord/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "result").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getAreYouWinningSon(guess:String):Boolean={
    val url = s"$baseurl/contoller/getAreYouWinningSon/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "won").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getVersuche():Int={
    val url = s"$baseurl/contoller/getVersuche"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "versuche").as[Int] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getEvaluateGuess(guess:String):Map[Int, String]={
    val url = s"$baseurl/contoller/getEvaluateGuess/$guess" // URL von Microservice B

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

  def putMove(versuche:Int, feedback:Map[Int, String]):Unit={
    val url = s"$baseurl/contoller/putMove/$versuche"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def patchVersuche(versuche:Int):Unit={
    val url = s"$baseurl/contoller/patchVersuche/$versuche"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def patchChangeState(level:Int):Unit={
    val url = s"$baseurl/contoller/patchChangeState/$level"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def putCreateGameboard():Unit={
    val url = s"$baseurl/contoller/putCreateGameboard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }
  def putCreateWinningBoard():Unit={
    val url = s"$baseurl/contoller/putCreateWinningBoard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def putUndoMove():Unit={
    val url = s"$baseurl/contoller/putUndoMove"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def postGameSave():Unit={
    val url = s"$baseurl/contoller/postGameSave"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
  }

  def getGameSave():String={
    val url = s"$baseurl/contoller/getGameSave"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "message").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getGameBoard():String={
    val url = s"$baseurl/contoller/getGameBoard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "gameboard").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }

  def getTargetwordString():String={
    val url = s"$baseurl/contoller/getTargetWordString"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Blockiere auf das Future und warte auf die Antwort
    val response = Await.result(responseFuture, 5.seconds)

    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
    val entityFuture = response.entity.toStrict(5.seconds)
    val entity = Await.result(entityFuture, 5.seconds)

    val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
    (jsonResponse \ "targetWord").as[String] // Das "continue"-Feld extrahieren und zurückgeben
  }



}

