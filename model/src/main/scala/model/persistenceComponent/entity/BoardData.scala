package model.persistenceComponent.entity

import model.gamefieldComponent.GamefieldInterface
import play.api.libs.json.{JsObject, JsValue, Json}

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

  def gameboardFromJason(seq: Seq[JsValue]): Map[Int, Map[Int, String]] = {
    val resultMap: Map[Int, Map[Int, String]] = seq.map { jsValue =>
      val key = (jsValue \ "key").as[Int]
      val gameField = (jsValue \ "gamefield").as[Map[String, String]].map {
        case (k, v) => k.toInt -> v
      }
      key -> gameField
    }.toMap

    resultMap
  }
}
