package aview

import scala.concurrent.Await
import scala.concurrent.duration.Duration


object UIServer {
  def main(args: Array[String]): Unit = {
    println("Starte UI-Server...")
    val uiApi = new UIApi() // ← Das startet dann automatisch den HTTP-Server
    
    // Warten bis der Server beendet wird
    Await.result(uiApi.system.whenTerminated, Duration.Inf)
  }
}
