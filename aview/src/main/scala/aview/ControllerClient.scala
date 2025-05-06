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

  def getCount(): Boolean = {
    val url = s"$baseurl/getCount"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "continue").as[Boolean]
  }

  def getGuessTransform(input: String): String = {
    val url = s"$baseurl/getGuessTransform/$input"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "transformedGuess").as[String]
  }

  def getControllLength(length: Int): Boolean = {
    val url = s"$baseurl/getControllLength/$length"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Boolean]
  }

  def getControllRealWord(guess: String): Boolean = {
    val url = s"$baseurl/getControllRealWord/$guess"
    val request = HttpRequest(HttpMethods.GET, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

    val entity = Await.result(response.entity.toStrict(30.seconds), 30.seconds)
    val jsonResponse = Json.parse(entity.data.utf8String)
    (jsonResponse \ "result").as[Boolean]
  }

  def getAreYouWinningSon(guess: String): Boolean = {
    val url = s"$baseurl/getAreYouWinningSon/$guess"
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
    val url = s"$baseurl/getEvaluateGuess/$guess"
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
    val url = s"$baseurl/putMove/$versuche"
    val json: JsValue = Json.toJson(feedback)
    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))

    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = s"$baseurl/putMove/$versuche",
      entity = entity
    )

    Http().singleRequest(request)
  }

  def putVersuche(versuche: Int): Unit = {
    val url = s"$baseurl/putVersuche/$versuche"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)

//    if (response.status == OK) {
//      println("Versuche erfolgreich gepatcht.")
//    } else {
//      println(s"Fehler beim Patchen der Versuche: ${response.status}")
//    }
  }

  def patchChangeState(level: Int): Unit = {
    val url = s"$baseurl/patchChangeState/$level"
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
    val url = s"$baseurl/putGame/$name"
    val request = HttpRequest(HttpMethods.PUT, uri = url)
    val response = Await.result(Http().singleRequest(request), 30.seconds)
  }


}

