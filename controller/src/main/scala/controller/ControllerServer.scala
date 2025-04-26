package controller



object ControllerServer {
  def main(args: Array[String]): Unit = {
    println("Starte Controller-Server...")

    //Clients bauen
    val gameClient = new GameClient("http://localhost:8082/model/game")
    val fileClient = new FileIOClient("http://localhost:8082/model/fileIO")
    val observerClient = new ObserverClient("http://localhost:8080/ui")

    //Controller erzeugen
    given controller: ControllerInterface = new controll(gameClient, fileClient, observerClient)
    new ControllerApi() // ← Das startet dann automatisch den HTTP-Server
  }
}

