package rest

import model.FileIOComponent.{FileIOInterface, FileIOJSON}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object RestServer {
  def main(args: Array[String]): Unit = {
    println("Starte Rest-Server...")
    
    val restApi = new RestApi()
    Await.result(restApi.system.whenTerminated, Duration.Inf)
  }
}
