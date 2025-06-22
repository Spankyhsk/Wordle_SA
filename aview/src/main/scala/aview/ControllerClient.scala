package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import play.api.libs.json.*

import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}
import scala.concurrent.duration.*


class ControllerClient(baseurl:String)() {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  
  def startGame(): Unit = {
    val url = s"$baseurl/startGame"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)
  }

  def getCount(): Boolean = {
    val url = s"$baseurl/getCount"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "continue").as[Boolean]
  }

  def getGuessTransform(input: String): String = {
    val url = s"$baseurl/getGuessTransform?guess=$input"
    print(url)
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "transformedGuess").as[String]
  }

  def getControllLength(length: Int): Boolean = {
    val url = s"$baseurl/getControllLength?length=$length"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Boolean]
  }

  def getControllRealWord(guess: String): Boolean = {
    val url = s"$baseurl/getControllRealWord?guess=$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Boolean]
  }

  def getAreYouWinningSon(guess: String): Boolean = {
    val url = s"$baseurl/getAreYouWinningSon?guess=$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "won").as[Boolean]
  }

  def getVersuche(): Int = {
    val url = s"$baseurl/getVersuche"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "versuche").as[Int]
  }

  def getEvaluateGuess(guess: String): Map[Int, String] = {
    val url = s"$baseurl/getEvaluateGuess?guess=$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    val map: Map[Int, String] = jsonResponse.as[Map[String, String]].map {
      case (k, v) => k.toInt -> v
    }
    map
  }

  def putMove(versuche: Int, feedback: Map[Int, String]): Unit = {
    val url = s"$baseurl/putMove?move=$versuche"
    val json: JsValue = Json.toJson(feedback)
    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))

    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = url,
      entity = entity
    )

    Http().singleRequest(request)
  }

  def putVersuche(versuche: Int): Unit = {
    val url = s"$baseurl/putVersuche?versuche=$versuche"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == OK) {
//      println("Versuche erfolgreich gepatcht.")
//    } else {
//      println(s"Fehler beim Patchen der Versuche: ${response.status}")
//    }
  }

  def patchChangeState(level: Int): Unit = {
    val url = s"$baseurl/patchChangeState?state=$level"
    val request = HttpRequest(HttpMethods.PATCH, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == OK) {
//      println(s"Erfolgreich den Zustand auf Level $level geändert.")
//    } else {
//      println(s"Fehler beim Ändern des Zustands: ${response.status}")
//    }
  }

  def postGameSave(): Unit = {
    val url = s"$baseurl/postGameSave"
    val request = HttpRequest(HttpMethods.POST, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == OK) {
//      println("Spiel erfolgreich gespeichert.")
//    } else {
//      println(s"Fehler beim Speichern des Spiels: ${response.status}")
//    }
  }

  def getGameSave(): String = {
    val url = s"$baseurl/getGameSave"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "message").as[String]
  }

  def getGameBoard(): String = {
    val url = s"$baseurl/getGameBoard"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "gameboard").as[String]
  }

  def getTargetwordString(): String = {
    val url = s"$baseurl/getTargetWordString"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "targetWord").as[String]
  }

  def putCreateGameboard(): Unit = {
    val url = s"$baseurl/putCreateGameboard"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == StatusCodes.OK) {
//      println("Das Spielfeld wurde erfolgreich erstellt.")
//    } else {
//      println(s"Fehler beim Erstellen des Spielfelds: ${response.status}")
//    }
  }

  def putCreateWinningBoard(): Unit = {
    val url = s"$baseurl/putCreateWinningBoard"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == StatusCodes.OK) {
//      println("Das Gewinn-Board wurde erfolgreich erstellt.")
//    } else {
//      println(s"Fehler beim Erstellen des Gewinn-Boards: ${response.status}")
//    }
  }

  def putUndoMove(): Unit = {
    val url = s"$baseurl/putUndoMove"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == StatusCodes.OK) {
//      println("Der Zug wurde erfolgreich rückgängig gemacht.")
//    } else {
//      println(s"Fehler beim Rückgängigmachen des Zuges: ${response.status}")
//    }
  }
  
  def putGame(name:String):Unit ={
    val url = s"$baseurl/putGame?name=$name"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)
  }

  def getGame(gameId:Long): Unit = {
    val url = s"$baseurl/getGame?gameId=$gameId"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)
  }

  def search(): String = {
    val url = s"$baseurl/search"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "message").as[String]
  }
}

