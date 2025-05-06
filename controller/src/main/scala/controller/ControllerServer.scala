package controller

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object ControllerServer {
  def main(args: Array[String]): Unit = {
    println("Starte Controller-Server...")

    //Clients bauen
    val gameClient = new GameClient("http://model-service:8082/model/game")
    val fileClient = new FileIOClient("http://model-service:8082/model/fileIO")
    val observerClient = new ObserverClient("http://aview-service:8080/ui")
    val persistenceClient = new PersistenceClient("http://model-service:8082/persistence")

    //Controller erzeugen
    given controller: ControllerInterface = new controll(gameClient, fileClient, observerClient, persistenceClient)
    val controllerApi = new ControllerApi() // ← Das startet dann automatisch den HTTP-Server
    Await.result(controllerApi.system.whenTerminated, Duration.Inf)
  }
}

