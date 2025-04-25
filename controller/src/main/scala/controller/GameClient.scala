package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import model.gamefieldComponent.{GamefieldInterface, gameboard}
import model.gamemechComponent.gamemechInterface
import play.api.libs.json.{Format, JsError, JsResult, JsValue, Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}


class GameClient(baseurl:String)(implicit system: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext) {

  implicit val gamefieldFormat: Format[GamefieldInterface[GamefieldInterface[String]]] = new Format[GamefieldInterface[GamefieldInterface[String]]] {
    override def reads(json: JsValue): JsResult[GamefieldInterface[GamefieldInterface[String]]] = {
      json.validate[Map[Int, Map[Int, String]]].map { boardMap =>
        GamefieldFactory.createGameboard().setMap(boardMap)
      }
    }

    override def writes(o: GamefieldInterface[GamefieldInterface[String]]): JsValue = {
      val boardMap = o.getMap().map { case (key, gameField) =>
        key -> gameField.getMap()
      }
      Json.toJson(boardMap)
    }
  }

  private object GamefieldFactory {
    def createGameboard(): GamefieldInterface[GamefieldInterface[String]] = new gameboard()
  }
  

  def count(): Future[Boolean] = {
    val url = s"$baseurl/count"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Boolean umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        (Json.parse(jsonString) \ "continue").as[Boolean]
      }
    }
  }

  def controllLength(n: Int): Future[Boolean] = {
    val url = s"$baseurl/controllLength/$n"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Boolean umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        (Json.parse(jsonString) \ "result").as[Boolean]
      }
    }
  }

  def controllRealWord(guess: String): Future[Boolean] = {
    val url = s"$baseurl/controllRealWord/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Boolean umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        (Json.parse(jsonString) \ "result").as[Boolean]
      }
    }
  }

  def evaluateGuess(guess: String): Future[Map[Int, String]] = {
    val url = s"$baseurl/evaluateGuess/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Map[Int, String] umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        Json.parse(jsonString).as[Map[Int, String]]
      }
    }
  }

  def guessTransform(guess: String): Future[String] = {
    val url = s"$baseurl/GuessTransform/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein String umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String]
    }
  }

  def setVersuche(zahl: Integer): Future[Unit] = {
    val url = s"$baseurl/setVersuche/$zahl"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Unit umgewandelt
    responseFuture.map(_ => ())
  }

  def getVersuche(): Future[Int] = {
    val url = s"$baseurl/getVersuche"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Int umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map(_.toInt)
    }
  }

  def areYouWinningSon(guess: String): Future[Boolean] = {
    val url = s"$baseurl/areYouWinningSon/$guess"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Boolean umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        (Json.parse(jsonString) \ "result").as[Boolean]
      }
    }
  }

  def createWinningBoard(): Future[Unit] = {
    val url = s"$baseurl/createwinningboard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Unit umgewandelt
    responseFuture.map(_ => ())
  }

  //-----------------------------------------------------------------------------
  //               //board
  //-----------------------------------------------------------------------------

  def getGamefield(): Future[GamefieldInterface[GamefieldInterface[String]]] = {
    val url = s"$baseurl/getGamefield"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein GamefieldInterface umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        Json.parse(jsonString).as[GamefieldInterface[GamefieldInterface[String]]]
      }
    }
  }

  def createGameboard(): Future[Unit] = {
    val url = s"$baseurl/createGameboard"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Unit umgewandelt
    responseFuture.map(_ => ())
  }

  def gameToString: Future[String] = {
    val url = s"$baseurl/toString"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein String umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String]
    }
  }

  //-----------------------------------------------------------------------------
  //               //Mode
  //-----------------------------------------------------------------------------
  
  def changeState(e: Int): Future[Unit] = {
    val url = s"$baseurl/changeState/$e"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Unit umgewandelt
    responseFuture.map(_ => ())
  }

  def getTargetword(): Future[Map[Int, String]] = {
    val url = s"$baseurl/getTargetword"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein Map[Int, String] umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String].map { jsonString =>
        Json.parse(jsonString).as[Map[Int, String]]
      }
    }
  }

  def targetWordToString(): Future[String] = {
    val url = s"$baseurl/TargetwordToString"
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = url))
    // Hier wird eine HTTP-Anfrage an die URL gesendet und das Ergebnis in ein String umgewandelt
    responseFuture.flatMap { response =>
      Unmarshal(response.entity).to[String]
    }
  }

}