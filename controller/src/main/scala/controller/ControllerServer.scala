package controller

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object ControllerServer {
  def main(args: Array[String]): Unit = {
    println("Starte Controller-Server...")
    
    val alpakkaController = new AlpakkaController

    //Clients bauen
//    val gameClient = new GameClient(sys.env.getOrElse("MODEL_URL", "http://localhost:8082") + "/model/game")
    val gameClient = new GameClient(alpakkaController)
    val fileClient = new FileIOClient(alpakkaController)
    val observerClient = new ObserverClient(alpakkaController)
    val persistenceClient = new PersistenceClient(alpakkaController)

    //Controller erzeugen
    given controller: ControllerInterface = new controll(gameClient, fileClient, observerClient, persistenceClient)
    val controllerApi = new ControllerApi() // ← Das startet dann automatisch den HTTP-Server
    Await.result(controllerApi.system.whenTerminated, Duration.Inf)
  }
}

