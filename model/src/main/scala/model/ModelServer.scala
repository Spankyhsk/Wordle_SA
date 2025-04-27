package model

import model.FileIOComponent.{FileIOInterface, FileIOJSON}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ModelServer {
  def main(args: Array[String]): Unit = {
    println("Starte Model-Server...")

    given GameInterface = new Game()
    given FileIOInterface = new FileIOJSON

    val modelApi = new ModelApi()
    Await.result(modelApi.system.whenTerminated, Duration.Inf)
  }
}
