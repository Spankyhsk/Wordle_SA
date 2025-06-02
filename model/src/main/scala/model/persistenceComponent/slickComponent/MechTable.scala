package model.persistenceComponent.slickComponent

import slick.jdbc.H2Profile.Table
import slick.lifted.{TableQuery, Tag}
import slick.ast.ScalaBaseType.stringType
import slick.ast.ScalaBaseType.intType
import slick.ast.ScalaBaseType.longType
import model.persistenceComponent.slickComponent.GameTable
import slick.model.ForeignKeyAction

class MechTable(tag:Tag) extends Table[(Long, String, Int, Long)](tag, "Mech"){

  def mechId = column[Long]("mechId", O.PrimaryKey, O.AutoInc)

  def winningboard = column[String]("winningboard_data")

  def versuche = column[Int]("versuche")

  def gameId = column[Long]("gameId")

  def * = (mechId, winningboard, versuche, gameId)

  // Fremdschlüssel definieren
  def gameFK = foreignKey(
    "fk_game", // Name des Constraints
    gameId, // Lokale Spalte (in OrderItems)
    TableQuery[GameTable] // Zieltabelle
  )(_.gameId, onDelete = ForeignKeyAction.Cascade)
}
