package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.slickComponent.GameTable
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

class GameDAO(db:Database) extends DAOInterface[String, Long]{
  
  private val gameTable = TableQuery[GameTable(_)]()
  
  def save(obj: String):Int={
    val games = (gameTable returning gameTable.map(_.gameId)) +=(
      None
    )
    val resultFuture = db.run(games)

    games
  }

  def findAll(): Seq[String]

  def findById(id: Long): String

  def update(id: Long, obj: String): Unit

  def delete(id: Long): Unit
}
