package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.ModeData
import model.persistenceComponent.slickComponent.ModeTable
import play.api.libs.json.{JsResult, JsValue, Json}
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.duration.*
import scala.concurrent.Await

class ModeDAO(db:Database) extends DAOInterface[ModeData, Long]{

  private val modeTable = TableQuery(ModeTable(_))

  override def save(obj: ModeData):Long={
    val insertQuery = (modeTable returning modeTable.map(_.modeId)) +=(
      0L,
      obj.targetWordMapToJson(),
      obj.limit,
      obj.gameId
    )
    val resultFuture = db.run(insertQuery)

    Await.result(resultFuture, 10.seconds)
    
  }

  //Werden wahrscheinlich nie benötigt werden
  override def findAll(): Seq[ModeData] = {
    val query = for{
      mode <- modeTable
    }yield{
      (mode.gameId, mode.targetword, mode.limit)
    }
    val resultFuture = db.run(query.result)
    val resultMech: Seq[(Long, String, Int)] = Await.result(resultFuture, 10.seconds)
    val modeSeq: Seq[ModeData] = resultMech.map{case (gameId, targetword, limit) =>
      ModeData(gameId, targetwordFromJson(targetword), limit)
    }
    modeSeq
  }

  override def findById(id: Long): ModeData = {
    val query = for {
      mode <- modeTable if mode.gameId === id
    } yield {
      (mode.targetword, mode.limit)
    }

    val resultFuture = db.run(query.result)
    val resultGame: Seq[(String, Int)] = Await.result(resultFuture, 5.seconds)
    val mechSeq: Seq[ModeData] = resultGame.map { case (targetword, limit) =>
      ModeData(id, targetwordFromJson(targetword), limit);
    }
    mechSeq.head
  }

  //wird aktuell nicht gebraucht
  override def update(id: Long, obj: ModeData): Unit = {
    val query = modeTable
      .filter(_.gameId === id)
      .map(mode => (mode.targetword, mode.limit))
      .update((obj.targetWordMapToJson(), obj.limit))

    val resultFuture = db.run(query)
  }

  //DARF NICHT genutzt werden
  override def delete(id: Long): Unit = {
    val query = modeTable
      .filter(_.modeId === id)
      .delete

    val resultFuture = db.run(query)
  }

  def targetwordFromJson(targetword: String): Map[Int, String] = {
    val json: JsValue = Json.parse(targetword)
    val result: JsResult[Map[Int, String]] = (json \ "winningBoard").validate[Map[Int, String]]
    result.get
  }
}
