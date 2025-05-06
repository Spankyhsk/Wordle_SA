package model.persistenceComponent.slickComponent

import slick.jdbc.H2Profile.Table
import slick.lifted.Tag
import slick.jdbc.H2Profile.api._
import model.persistenceComponent.entity.GameData
import slick.jdbc.PostgresProfile.api._


class GameTable(tag:Tag) extends Table[GameData](tag, "Game"){
  def gameId = column[Long]("gameId", O.PrimaryKey, O.AutoInc)
  
  def * = (gameId.?).mapTo[GameData]
}
