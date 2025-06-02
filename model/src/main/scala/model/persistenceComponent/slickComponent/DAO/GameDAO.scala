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

  //Wird vermutlich nie benötigt
  override def findById(id: Long): String = {
    val query = for {
      game <-  gameTable if game.gameId === id
    }yield{
      (game.name)
    }
    val resultFuture = db.run(query.result)
    val resultGame: Seq[String] = Await.result(resultFuture, 5.seconds)
    val gameSeq: Seq[String] = resultGame.map{case (name) =>
      name
    }
    gameSeq.head
  }

  //wird aktuell nicht gebraucht
  override def update(id: Long, obj: String): Unit = {
    val query = gameTable
    .filter(_.gameId === id)
    .map(game => (game.name))
    .update(obj)
    
    val resultFuture = db.run(query)
  }

  //wird aktuell nicht gebraucht
  override def delete(id: Long): Unit = {
    val query = gameTable
    .filter(_.gameId === id)
    .delete
    
    val resultFuture =db.run(query)
  }
}
