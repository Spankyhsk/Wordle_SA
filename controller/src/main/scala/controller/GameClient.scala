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
import scala.concurrent.{Promise, Await, ExecutionContext, ExecutionContextExecutor, Future}


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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("count", promise)
    
    val command = ModelCommand("count", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("continue").flatMap(_.asBoolean).getOrElse(
      throw new RuntimeException("Kein 'continue'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von count: $resultData")
    resultData
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("controllLength", promise)
    
    val command = ModelCommand("controllLength", Map("length" -> n.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
    
    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("controllLength").flatMap(_.asBoolean).getOrElse(
      throw new RuntimeException("Kein 'result'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von controllLength: $resultData")
    resultData
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("controllRealWord", promise)
    
    val command = ModelCommand("controllRealWord", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("controllRealWord").flatMap(_.asBoolean).getOrElse(
      throw new RuntimeException("Kein 'result'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von controllRealWord: $resultData")
    resultData
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("evaluateGuess", promise)
    
    val command = ModelCommand("evaluateGuess", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    val result = Await.result(promise.future, 5.seconds)
    val data = result.data.get("evaluateGuess").getOrElse(
      throw new RuntimeException("Kein 'evaluateGuess'-Feld im Ergebnis gefunden")
    )
    
    val map = io.circe.parser.decode[Map[Int, String]](data.toString) match {
      case Right(value) => value
      case Left(error) => throw new RuntimeException(s"Fehler beim Parsen von 'evaluateGuess': $error")
    }
    println(s"✅ Ergebnis von evaluateGuess: $map")
    map
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("guessTransform", promise)

    val command = ModelCommand("guessTransform", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    print(s"Sending command to guessTransform: $commandJson")

    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    val result = Await.result(promise.future, 5.seconds)
    val transformed = result.data.get("transformedGuess").flatMap(_.asString).getOrElse(
      throw new RuntimeException("Kein 'transformedGuess'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von guessTransform: $transformed")
    transformed
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("getN", promise)
    
    val command = ModelCommand("getN", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
    
    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("getN").flatMap(_.asNumber.flatMap(_.toInt)).getOrElse(
      throw new RuntimeException("Kein 'getN'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von getVersuche: $resultData")
    resultData
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("areYouWinningSon", promise)
    
    val command = ModelCommand("areYouWinningSon", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
    
    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("areYouWinningSon").flatMap(_.asBoolean).getOrElse(
      throw new RuntimeException("Kein 'won'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von areYouWinningSon: $resultData")
    resultData
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("gameboard", promise)
    
    val command = ModelCommand("gameboard", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
    
    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("gameboard").flatMap(_.asString).getOrElse(
      throw new RuntimeException("Kein 'gameboard'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von gameToString: $resultData")
    resultData
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("TargetwordToString", promise)
    
    val command = ModelCommand("TargetwordToString", null)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    val result = Await.result(promise.future, 5.seconds)
    val resultData = result.data.get("targetWord").flatMap(_.asString).getOrElse(
      throw new RuntimeException("Kein 'targetWord'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von targetWordToString: $resultData")
    resultData
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