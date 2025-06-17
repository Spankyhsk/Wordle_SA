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
import play.api.libs.json.{Format, JsError, JsResult, JsValue, Json => PlayJson, OFormat}
//import controller.AlpakkaController.*
import io.circe.syntax._ // für .asJson
import io.circe.generic.auto._ // erstellt Encoder/Decoder automatisch

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}

import org.apache.kafka.clients.producer.ProducerRecord
import io.circe.Json
import io.circe.generic.auto._ // oder: import io.circe.generic.semiauto.deriveEncoder
import io.circe.parser.decode

class GameClient(alpakkaController: AlpakkaController)() {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = Materializer(system)
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  
//  val alpakkaController = AlpakkaController

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
      PlayJson.toJson(boardMap)
    }
  }

  private object GamefieldFactory {
    def createGameboard(): GamefieldInterface[GamefieldInterface[String]] = new gameboard()
  }

  def count(): Boolean = {
//    val url = s"$baseurl/count"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//
//    // Verarbeite die Antwort und extrahiere den "continue"-Boolean
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "continue").as[Boolean] // Das "continue"-Feld extrahieren und zurückgeben
    val command = ModelCommand("count", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("count") match{
      case Some(result) => result.data.get("continue").flatMap(_.asBoolean).getOrElse(
        throw new RuntimeException("Kein 'continue'-Feld im Ergebnis gefunden")
      )
    }
  }

  def controllLength(n: Int): Boolean = {
//    val url = s"$baseurl/controllLength?length=$n"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//
//    // Verarbeite die Antwort und extrahiere den "result"-Boolean
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "result").as[Boolean] // Das "result"-Feld extrahieren und zurückgeben
    val command = ModelCommand("controllLength", Map("length" -> n.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("controllLength") match {
      case Some(result) => result.data.get("result").flatMap(_.asBoolean).getOrElse(
        throw new RuntimeException("Kein 'result'-Feld im Ergebnis gefunden")
      )
    }
  }

  def controllRealWord(guess: String): Boolean = {
//    val url = s"$baseurl/controllRealWord?guess=$guess"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//    // Verarbeite die Antwort und extrahiere den "result"-Boolean
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "result").as[Boolean] // Das "result"-Feld extrahieren und zurückgeben
    val command = ModelCommand("controllRealWord", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("controllRealWord") match {
      case Some(result) => result.data.get("result").flatMap(_.asBoolean).getOrElse(
        throw new RuntimeException("Kein 'result'-Feld im Ergebnis gefunden")
      )
      case None => throw(new RuntimeException("controllRealWord aufruf hat nicht richtig geklappt"))
    }
  }

  def evaluateGuess(guess: String): Map[Int, String] = {
//    val url = s"$baseurl/evaluateGuess?guess=$guess"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//
//    // Antwort verarbeiten
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    // JSON parsen und als Map[Int, String] zurückgeben
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    jsonResponse.as[Map[Int, String]]
    val command = ModelCommand("evaluateGuess", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("evaluateGuess") match {
      case Some(result) => result.data.get("result").asInstanceOf[Map[Int, String]]
      case None => throw(new RuntimeException("evaluateGuess aufruf hat nicht richtig geklappt"))
    }
  }

  def guessTransform(guess: String): String = {
//    val url = s"$baseurl/GuessTransform?guess=$guess"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//    // Verarbeite die Antwort und extrahiere den "transformedGuess"-String
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "transformedGuess").as[String] // Das "transformedGuess"-Feld extrahieren und zurückgeben
    val command = ModelCommand("guessTransform", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("guessTransform") match {
      case Some(result) => result.data.get("transformedGuess").flatMap(_.asString).getOrElse(
        throw new RuntimeException("Kein 'transformedGuess'-Feld im Ergebnis gefunden")
      )
      case None => throw(new RuntimeException("guessTransform aufruf hat nicht richtig geklappt"))
    }
  }

  def setVersuche(zahl: Integer): Unit = {
//    val url = s"$baseurl/setN?versuche=$zahl"
//    val request = HttpRequest(HttpMethods.PUT, uri = url)
//    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
    val command = ModelCommand("setN", Map("versuche" -> zahl.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def getVersuche(): Int = {
//    val url = s"$baseurl/getN"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//    // Verarbeite die Antwort und extrahiere den "result"-Integer
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "result").as[Int] // Das "result"-Feld extrahieren und zurückgeben
    val command = ModelCommand("getN", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("getN") match {
      case Some(result) => result.data.get("result").asInstanceOf[Int]
      case None => throw(new RuntimeException("getN aufruf hat nicht richtig geklappt"))
    }
  }

  def areYouWinningSon(guess: String): Boolean = {
//    val url = s"$baseurl/areYouWinningSon?guess=$guess"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//    // Verarbeite die Antwort und extrahiere den "won"-Boolean
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "won").as[Boolean] // Das "won"-Feld extrahieren und zurückgeben
    val command = ModelCommand("areYouWinningSon", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("areYouWinningSon") match {
      case Some(result) => result.data.get("won").flatMap(_.asBoolean).getOrElse(
        throw new RuntimeException("Kein 'won'-Feld im Ergebnis gefunden")
      )
      case None => throw(new RuntimeException("areYouWinningSon aufruf hat nicht richtig geklappt"))
    }
  }

  def createWinningBoard(): Unit = {
//    val url = s"$baseurl/createwinningboard"
//    val request = HttpRequest(HttpMethods.PUT, uri = url)
//    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
    val command = ModelCommand("createwinningboard", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def createGameboard(): Unit = {
//    val url = s"$baseurl/createGameboard"
//    val request = HttpRequest(HttpMethods.PUT, uri = url)
//    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
    val command = ModelCommand("createGameboard", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def gameToString: String = {
//    val url = s"$baseurl/toString"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//    // Verarbeite die Antwort und extrahiere den "gameboard"-String
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "gameboard").as[String] // Das "gameboard"-Feld extrahieren und zurückgeben
    val command = ModelCommand("toString", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("toString") match {
      case Some(result) => result.data.get("gameboard").flatMap(_.asString).getOrElse(
        throw new RuntimeException("Kein 'gameboard'-Feld im Ergebnis gefunden")
      )
      case None => throw(new RuntimeException("toString aufruf hat nicht richtig geklappt"))
    }
  }

  def changeState(e: Int): Unit = {
//    val url = s"$baseurl/changeState?level=$e"
//    val request = HttpRequest(HttpMethods.PATCH, uri = url)
//    Await.result(Http().singleRequest(request), 30.seconds) // Warte auf die Antwort
    val command = ModelCommand("changeState", Map("level" -> e.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def targetWordToString(): String = {
//    val url = s"$baseurl/TargetwordToString"
//    val request = HttpRequest(HttpMethods.GET, uri = url)
//    val response = Await.result(Http().singleRequest(request), 30.seconds)
//    // Verarbeite die Antwort und extrahiere den "targetWord"-String
//    val entityFuture = response.entity.toStrict(30.seconds)
//    val entity = Await.result(entityFuture, 30.seconds)
//    val jsonResponse = Json.parse(entity.data.utf8String)
//    (jsonResponse \ "targetWord").as[String] // Das "targetWord"-Feld extrahieren und zurückgeben
    val command = ModelCommand("TargetwordToString", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    alpakkaController.resultCache.get("TargetwordToString") match {
      case Some(result) => result.data.get("targetWord").flatMap(_.asString).getOrElse(
        throw new RuntimeException("Kein 'targetWord'-Feld im Ergebnis gefunden")
      )
      case None => throw(new RuntimeException("TargetwordToString aufruf hat nicht richtig geklappt"))
    }
  }


  //--------------------------------------------------------------------
  def step(key:Int, feedback:Map[Int,String]):Unit={
//    val json: JsValue = Json.toJson(feedback)
//    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))
//    val request = HttpRequest(
//      method = HttpMethods.POST,
//      uri = s"$baseurl/step?key=$key",
//      entity = entity
//    )
//    Http().singleRequest(request)
    val command = ModelCommand("step", Map("key" -> key.asJson, "feedback" -> feedback.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def undoStep(key:Int, feedback:Map[Int, String]):Unit={
//    val json: JsValue = Json.toJson(feedback)
//    val entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(json))
//    val request = HttpRequest(
//      method = HttpMethods.POST,
//      uri = s"$baseurl/undoStep?key=$key",
//      entity = entity
//    )
//    Http().singleRequest(request)
    val command = ModelCommand("undoStep", Map("key" -> key.asJson, "feedback" -> feedback.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

}