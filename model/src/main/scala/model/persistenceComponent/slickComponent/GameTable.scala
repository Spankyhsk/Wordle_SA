package model.persistenceComponent.slickComponent

import slick.jdbc.H2Profile.Table
import slick.lifted.Tag
import slick.ast.ScalaBaseType.longType


class GameTable(tag:Tag) extends Table[(Option[Long])](tag, "Game"){
  def id = column[Option[Long]]("gameId", O.PrimaryKey, O.AutoInc)
  
  def * = (id)
}
