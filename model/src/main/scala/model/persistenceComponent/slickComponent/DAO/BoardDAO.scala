package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.BoardData
import model.persistenceComponent.slickComponent.BoardTable
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

class BoardDAO(db:Database) extends DAOInterface[BoardData, Long]{
  
  private val boardTable = TableQuery(BoardTable(_))
  
  def save(obj: BoardData): Long ={
    val board = (boardTable returning boardTable.map(_.id)) += (
      None,
      obj.boardMap,
      obj.gameId
    )
    val resultFuture = db.run(board)

    board
  }

  def findAll(): Seq[BoardData]

  def findById(id: Long): BoardData

  def update(id: Long, obj: BoardData): Unit

  def delete(id: Long): Unit
}
