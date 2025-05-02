package model.persistenceComponent.slickComponent

import slick.lifted.{TableQuery, Tag}
import slick.jdbc.H2Profile.Table
import slick.model.ForeignKeyAction
import slick.ast.ScalaBaseType.stringType
import slick.ast.ScalaBaseType.intType
import slick.ast.ScalaBaseType.longType


class ModeTable(tag:Tag) extends Table[(Option[Long], String, Int, Long)](tag, "Mode"){

  def id = column[Option[Long]]("modeId", O.PrimaryKey, O.AutoInc)
  def targetword = column[String]("targetword_data")
  def limit = column[Int]("limit")
  def gameId = column[Long]("gameId")
  
  def * = (id, targetword, limit, gameId)

  // Fremdschlüssel definieren
  def gameFK = foreignKey(
    "fk_game", // Name des Constraints
    gameId, // Lokale Spalte (in OrderItems)
    TableQuery[GameTable] // Zieltabelle
  )(_.id, onDelete = ForeignKeyAction.Cascade)
  
}
