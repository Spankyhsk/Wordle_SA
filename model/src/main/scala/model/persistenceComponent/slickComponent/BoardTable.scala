package model.persistenceComponent.slickComponent

import slick.lifted.{TableQuery, Tag}
import slick.jdbc.H2Profile.Table
import slick.ast.ScalaBaseType.stringType
import slick.ast.ScalaBaseType.longType
import slick.model.ForeignKeyAction
import model.persistenceComponent.slickComponent.GameTable


class BoardTable(tag:Tag) extends Table[(Long,String, Long)](tag, "Board"){
  def boardId = column[Long]("boardId", O.PrimaryKey, O.AutoInc)
  def boardMap = column[String]("gameboard_data")
  def gameId = column[Long]("gameId")

  def gameFk =foreignKey(
    "fk_game", // Name des Constraints
    gameId, // Lokale Spalte (in OrderItems)
    TableQuery[GameTable] // Zieltabelle
  )(_.gameId, onDelete = ForeignKeyAction.Cascade)

  def * = (boardId, boardMap,gameId)

  
}
