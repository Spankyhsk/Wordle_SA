package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.BoardData
import model.persistenceComponent.slickComponent.BoardTable
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.Await
import scala.concurrent.duration.*


class BoardDAO(db:Database) extends DAOInterface[BoardData, Long]{
  
  private val boardTable = TableQuery(BoardTable(_))

  override def save(obj: BoardData): Long ={
    val insertQuery = (boardTable returning boardTable.map(_.boardId)) += (
      0L,
      obj.boardMap,
      obj.gameId
    )
    val resultFuture = db.run(insertQuery)

    Await.result(resultFuture, 10.seconds)
  }

  override def findAll(): Seq[BoardData] = ???

  override def findById(id: Long): BoardData = {
    val query = for {
      board <- boardTable if board.gameId === id
    } yield {
      (board.boardMap)
    }

    val resultFuture = db.run(query.result)
    val resultGame: Seq[String] = Await.result(resultFuture, 5.seconds)
    val BoardSeq: Seq[BoardData] = resultGame.map { case (boardMap) =>
      BoardData(id, boardMap);
    }
    BoardSeq.head
  }

  override def update(id: Long, obj: BoardData): Unit = ???

  override def delete(id: Long): Unit = ???

  
}
