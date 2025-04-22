package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._


class ControllerClient(baseurl:String)(implicit system: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext) {

  def getCount():Future[Boolean]={
    val url = s"$baseurl/count"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    responseFuture.flatMap { response =>
      // Wenn die Antwort OK ist, entpacken wir sie zu einem Boolean
      response.entity.toStrict(5.seconds).map { entity =>
        val jsonResponse = Json.parse(entity.data.utf8String) // JSON aus dem Body extrahieren
        val continue = (jsonResponse \ "continue").as[Boolean] // "continue" extrahieren
        continue
      }
    }
  }

  def getGuessTransform(input:String):Future[String]={

  }

  def getControllLength(length:Int):Future[Boolean]={

  }

  def getControllRealWord(guess:String):Future[Boolean]={

  }

  def getAreYouWinningSon(guess:String):Future[Boolean]={

  }

  def getVersuche():Future[Int]={

  }

  def getEvaluateGuess(guess:String):Future[Map[Int, String]]={
    val url = s"$baseurl/get-map/$guess" // URL von Microservice B

    // HTTP-Request an Microservice B
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))

    // Antwort verarbeiten
    responseFuture.flatMap { response =>
      // Warten auf den Inhalt und den JSON-Body verarbeiten
      response.entity.toStrict(5.seconds).map { entity =>
        val jsonResponse = Json.parse(entity.data.utf8String) // JSON parsen
        jsonResponse.as[Map[Int, String]] // Map extrahieren
      }
    }
  }
  
  def putMove(versuche:Int, feedback:Map[Int, String]):Unit={
    
  }
  
  def patchVersuche(versuche:Int):Unit={
    
  }
  
  def patchChangeState(level:Int):Unit={
    
  }
  
  def putCreateGameboard():Unit={
    
  }
  def putCreateWinningBoard():Unit={
    
  }
  
  def putUndoMove():Unit={
    
  }
  
  def postGameSave():Unit={
    
  }
  
  def getGameSave():Future[String]={
    
  }
  
  def getGameBoard():Future[String]={
    
  }
  
  def getTargetword():Future[String]={
    
  }



}

