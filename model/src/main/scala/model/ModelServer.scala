package model

import model.FileIOComponent.{FileIOInterface, FileIOJSON}

object ModelServer {
  def main(args: Array[String]): Unit = {
    println("Starte Model-Server...")

    given GameInterface = new Game()
    given FileIOInterface = new FileIOJSON

    val modelApi = new ModelApi()
  }
}
