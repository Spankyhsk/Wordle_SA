package de.htwg.se.wordle


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethod, HttpMethods, HttpRequest}
import akka.util.ByteString
import com.google.inject.Guice
import aview.{ControllerClient, GUISWING, TUI, UIApi}
import controller.ControllerInterface
import model.ModelApi
import controller.ControllerApi
import de.htwg.se.wordle.Default.given
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
    val welcome = Await.result(callApi(HttpMethods.GET, "http://localhost:8080/ui/tui"), 30.seconds)
    println(welcome)

    while (true) {
      // Check auf neues Spiel
      val newGameJson = Await.result(callApi(HttpMethods.GET, "http://localhost:8080/ui/tui/getNewGame"), 30.seconds)
      val newGame = (Json.parse(newGameJson) \ "newGame").as[Boolean]

      if (newGame) {
        val selectText = Await.result(callApi(HttpMethods.GET, "http://localhost:8080/ui/tui/Select"), 30.seconds)
        println(selectText)
      }

      // Nutzerinput lesen
      val input = StdIn.readLine()

      if (input == "$quit") {
        println("Spiel beendet.")
        system.terminate()
        return
      }

      // Input an Server senden
      val response = Await.result(callApi(HttpMethods.PUT, s"http://localhost:8080/ui/tui/processInput/$input"), 30.seconds)
      //if (response.nonEmpty) println(response)
    }
  }
}

