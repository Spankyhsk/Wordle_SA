package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpEntity

import scala.concurrent.TimeoutException
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("Count", promise)

    val command = ModelCommand("count", Map.empty)
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("ControllLength", promise)

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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("ControllRealWord", promise)

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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("EvaluateGuess", promise)

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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("GuessTransform", promise)
    println(s"Checking akpakkaController.pendingResults for guessTransform: ${alpakkaController.pendingResults}")
    val command = ModelCommand("guessTransform", Map("guess" -> guess.asJson))
    val commandJson = command.asJson.noSpaces
    print(s"Sending command to guessTransform: $commandJson")

    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)

    try {
      val result = Await.result(promise.future, 10.seconds)
      val transformed = result.data.get("transformedGuess").flatMap(_.asString).getOrElse(
        throw new RuntimeException("Kein 'transformedGuess'-Feld im Ergebnis gefunden")
      )
      println(s"✅ Ergebnis von guessTransform: $transformed")
      transformed
    } catch {
      case ex: TimeoutException =>
        alpakkaController.pendingResults.remove("GuessTransform")
        throw ex
    }
  }

  def setVersuche(zahl: Integer): Unit = {
    val command = ModelCommand("setN", Map("versuche" -> zahl.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def getVersuche(): Int = {
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("GetN", promise)

    val command = ModelCommand("getN", Map.empty)
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
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("AreYouWinningSon", promise)

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
    val command = ModelCommand("createwinningboard", Map.empty)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def createGameboard(): Unit = {
    val command = ModelCommand("createGameboard", Map.empty)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def gameToString: String = {
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("Gameboard", promise)
    println("Kommen wir hier eigentlich überhaupt rein?")
    // Sende den Befehl an den AlpakkaController, welcher im Model verarbeitet wird
    val command = ModelCommand("gameboard", Map.empty)
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    println(s"HIER ----- Sending command to gameToString: $commandJson")
    alpakkaController.send(record)

    val result = Await.result(promise.future, 10.seconds)
    val resultData = result.data.get("gameboard").flatMap(_.asString).getOrElse(
      throw new RuntimeException("Kein 'gameboard'-Feld im Ergebnis gefunden")
    )
    println(s"✅ Ergebnis von gameToString: $resultData")
    resultData
  }

  def changeState(e: Int): Unit = {
    val command = ModelCommand("changeState", Map("level" -> e.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def targetWordToString(): String = {
    val promise = Promise[ResultEvent]()
    alpakkaController.pendingResults.put("TargetwordToString", promise)

    val command = ModelCommand("targetwordToString", Map.empty)
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
    val command = ModelCommand("step", Map("key" -> key.asJson, "feedback" -> feedback.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }

  def undoStep(key:Int, feedback:Map[Int, String]):Unit={
    val command = ModelCommand("undoStep", Map("key" -> key.asJson, "feedback" -> feedback.asJson))
    val commandJson = command.asJson.noSpaces
    val record = new ProducerRecord[String, String]("model-commands", commandJson)
    alpakkaController.send(record)
  }
}