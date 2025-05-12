package model.persistenceComponent.entity

import model.gamefieldComponent.GamefieldInterface
import play.api.libs.json.Json


case class GameEntity(gameId: Long, name:String, winningBoard:Map[Int, Boolean], versuche:Int, gameBoard:String, targetword:Map[Int, String], limit:Int) {
  
  def winningBoardToJson():String={
    Json.prettyPrint(
      Json.obj(
        "winningBoard" -> winningBoard
      )
    )
  }

  def targetWordToJson(): String = {
    Json.prettyPrint(
      Json.obj(
        "TargetWord" -> targetword
      )
    )
  }
  
}
