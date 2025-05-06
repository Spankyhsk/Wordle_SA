package model.persistenceComponent.slickComponent

import slick.lifted.{TableQuery, Tag}
import slick.jdbc.H2Profile.Table
import slick.model.ForeignKeyAction
import slick.ast.ScalaBaseType.stringType
import slick.ast.ScalaBaseType.intType
import slick.ast.ScalaBaseType.longType



class ModeTable(tag:Tag) extends Table[(Long, String, Int, Long)](tag, "Mode"){

  def modeId = column[Long]("modeId", O.PrimaryKey, O.AutoInc)
  def targetword = column[String]("targetword_data")
  def limit = column[Int]("limit")
  def gameId = column[Long]("gameId")
  
  def * = (modeId, targetword, limit, gameId)

  // Fremdschlüssel definieren
  def gameFK = foreignKey(
    "fk_game", // Name des Constraints
    gameId, // Lokale Spalte (in OrderItems)
    TableQuery[GameTable] // Zieltabelle
  )(_.gameId, onDelete = ForeignKeyAction.Cascade)
  
}
