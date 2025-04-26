package aview

object UIServer {
  def main(args: Array[String]): Unit = {
    println("Starte UI-Server...")
    new UIApi() // ← Das startet dann automatisch den HTTP-Server
  }
}
