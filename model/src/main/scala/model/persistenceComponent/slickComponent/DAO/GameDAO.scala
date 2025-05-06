package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.GameData
import model.persistenceComponent.slickComponent.GameTable
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.Await
import scala.concurrent.duration.*

class GameDAO(db:Database) extends DAOInterface[String, Long]{
  
  val gameTable = TableQuery(GameTable(_))

  override def save(obj: String):Long={
    val insertQuery = (gameTable returning gameTable.map(_.gameId)) += GameData(None, obj)
    val resultFuture = db.run(insertQuery)
    Await.result(resultFuture, 10.seconds)
  }

  override def findAll(): Seq[String] = {
    val query = for{
      game <- gameTable
    }yield {
      (game.gameId, game.name)
    }

    val resultFuture = db.run(query.result)
    val resultGame: Seq[(Long, String)] = Await.result(resultFuture, 10.seconds)
    val gameSeq: Seq[String] = resultGame.map{case (gameId, name) =>
      s"$gameId := $name"
    }
    gameSeq
  }

  override def findById(id: Long): String = ???

  override def update(id: Long, obj: String): Unit = ???

  override def delete(id: Long): Unit = ???
}
