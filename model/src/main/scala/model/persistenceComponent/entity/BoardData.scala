package model.persistenceComponent.entity

import model.gamefieldComponent.GamefieldInterface
import play.api.libs.json.{JsArray, JsObject, JsString, JsValue, Json}

case class BoardData(gameId:Long, boardMap:Map[Int, GamefieldInterface[String]]) {

  def gameboardToJason(): String = {
    Json.prettyPrint(
      Json.obj(
      "gameboard" -> Json.toJson(
        for {
          key <- 1 until boardMap.size + 1
        } yield {
          Json.obj(
            "key" -> key,
            "gamefield" -> boardMap(key).getMap()
          )
        }
      )
      ))
  }

  def gameboardFromJason(gameboard:String): Map[Int, Map[Int, String]] = {

    val jsValue = Json.parse(gameboard)

    val gameboardArray = (jsValue \ "gameboard").as[JsArray]

    gameboardArray.value.map { entry =>
      val key = (entry \ "key").as[Int]

      val gamefieldJsObj = (entry \ "gamefield").as[JsObject]
      val gamefieldMap: Map[Int, String] = gamefieldJsObj.fields.map {
        case (k, JsString(v)) => k.toInt -> v
        case (k, v) => throw new RuntimeException(s"Unerwarteter Wert für $k: $v")
      }.toMap

      key -> gamefieldMap
    }.toMap
  }
}
