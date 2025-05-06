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

  override def findAll(): Seq[ModeData] = ???

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

  override def update(id: Long, obj: ModeData): Unit = ???

  override def delete(id: Long): Unit = ???

  def targetwordFromJson(targetword: String): Map[Int, String] = {
    val json: JsValue = Json.parse(targetword)
    val result: JsResult[Map[Int, String]] = (json \ "winningBoard").validate[Map[Int, String]]
    result.get
  }
}
