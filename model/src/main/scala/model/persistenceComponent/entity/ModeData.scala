package model.persistenceComponent.entity

import play.api.libs.json.Json

case class ModeData(gameId: Long, targetWordMap:Map[Int, String], limit:Int) {
  
  def targetWordMapToJson():String={
    Json.prettyPrint(
      Json.obj(
        "TargetWord" -> targetWordMap
      )
    )
  }
}
