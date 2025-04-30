package main

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.util.ByteString
import aview.{ControllerClient, GUISWING, TUI, UIApi}
import controller.{ControllerApi, ControllerInterface}
import main.Default.given
import model.ModelApi
import play.api.libs.json.Json

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.io.StdIn.readLine

object wordle {


  def callApi(method: HttpMethod, url: String, body: Option[String] = None)
             (implicit system: ActorSystem, ec: ExecutionContextExecutor): Future[String] = {
    val entity = body.map(b => HttpEntity(ContentTypes.`application/json`, b)).getOrElse(HttpEntity.Empty)
    val request = HttpRequest(method = method, uri = url, entity = entity)

    Http().singleRequest(request).flatMap { response =>
      response.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)
    }
  }

  def log(msg: String): Unit = println(s"[${java.time.LocalTime.now}] $msg")


  def main(args:Array[String]): Unit = {
    //    val injector = Guice.createInjector(new WordleModuleJson)
    //    val controll = injector.getInstance(classOf[ControllerInterface])
    //    val tui = new TUI(new ControllerClient("http://localhost:8081"))
    //    val gui = new GUISWING(new ControllerClient("http://localhost:8081"))

    implicit val system: ActorSystem = ActorSystem("WordleSystem")
    implicit val ec: ExecutionContextExecutor = system.dispatcher


    ModelApi() // Port 8082
    ControllerApi() // Port 8081
    UIApi() //Port 8080

    // Begrüßung über die API holen
    val welcome = Await.result(callApi(HttpMethods.GET, "http://aview-service:8080/ui/tui"), 30.seconds)
    println(welcome)
    log(s"Begrüßung erhalten: $welcome")
    while (true) {
      // Check auf neues Spiel
      val newGameJson = Await.result(callApi(HttpMethods.GET, "http://aview-service:8080/ui/tui/getNewGame"), 30.seconds)
      val newGame = (Json.parse(newGameJson) \ "newGame").as[Boolean]
      log(s"Antwort von /getNewGame: $newGameJson")

      if (newGame) {
        val selectText = Await.result(callApi(HttpMethods.GET, "http://aview-service:8080/ui/tui/Select"), 30.seconds)
        println(selectText)
        log(s"Antwort von /Select: $selectText")
      }

      log("Warte auf Benutzereingabe...")
      // Nutzerinput lesen
      val input = StdIn.readLine()
      log(s"Benutzereingabe: $input")

      if (input == "$quit") {
        println("Spiel beendet.")
        system.terminate()
        return
      }

      // Input an Server senden
      val response = Await.result(callApi(HttpMethods.PUT, s"http://aview-service:8080/ui/tui/processInput/$input"), 30.seconds)
      log(s"Antwort vom Server (input): $response")
      //if (response.nonEmpty) println(response)
    }
  }
}

