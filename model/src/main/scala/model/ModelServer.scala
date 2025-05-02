package model

import model.FileIOComponent.{FileIOInterface, FileIOJSON}
import model.persistenceComponent.PersistenceInterface
import model.persistenceComponent.slickComponent.SlickPersistenceImpl

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ModelServer {
  def main(args: Array[String]): Unit = {
    println("Starte Model-Server...")

    given GameInterface = new Game()
    given FileIOInterface = new FileIOJSON
    given PersistenceInterface = new SlickPersistenceImpl()

    val modelApi = new ModelApi()
    Await.result(modelApi.system.whenTerminated, Duration.Inf)
  }
}
