package model.persistenceComponent.entity

import play.api.libs.json.Json

case class MechData(gameId:Long, winningBoard:Map[Int,Boolean], versuche:Int) {

  def winningBoardTojson():String={
    Json.prettyPrint(
      Json.obj(
        "winningBoard" -> winningBoard
      )
    )
  }
}
