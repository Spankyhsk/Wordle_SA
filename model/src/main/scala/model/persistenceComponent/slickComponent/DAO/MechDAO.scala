package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.{MechData, ModeData}
import model.persistenceComponent.slickComponent.MechTable
import play.api.libs.json.{JsResult, JsValue, Json}
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.duration.*
import scala.concurrent.Await

class MechDAO(db:Database) extends DAOInterface[MechData, Long]{

  private val mechTable = TableQuery(MechTable(_))

  override def save(obj: MechData): Long={
    val insertQuery = (mechTable returning mechTable.map(_.mechId)) += (
      0L,
      obj.winningBoardTojson(),
      obj.versuche,
      obj.gameId
    )
    val resultFuture = db.run(insertQuery)
    Await.result(resultFuture, 10.seconds)
  }

  //Werden wahrscheinlich nie benötigt werden
  override def findAll(): Seq[MechData] = {
    val query = for{
      mech <- mechTable
    }yield{
      (mech.gameId, mech.winningboard, mech.versuche)
    }
    val resultFuture = db.run(query.result)
    val resultMech: Seq[(Long, String, Int)] = Await.result(resultFuture, 10.seconds)
    val mechSeq: Seq[MechData] = resultMech.map{case (gameId, winningboard, versuche) =>
      MechData(gameId, winningBoardFromJson(winningboard), versuche)
    }
    mechSeq
  }

  override def findById(id: Long): MechData = {
    val query = for {
      mech <- mechTable if mech.gameId === id
    }yield{
      (mech.winningboard, mech.versuche)
    }
    
    val resultFuture = db.run(query.result)
    val resultMech: Seq[(String, Int)] = Await.result(resultFuture, 5.seconds)
    val mechSeq:Seq[MechData] = resultMech.map{ case(winningboard, versuche) =>
      MechData(id, winningBoardFromJson(winningboard), versuche);
    }
    mechSeq.head
  }

  //wird aktuell nicht gebraucht
  override def update(id: Long, obj: MechData): Unit = {
    val query = mechTable
    .filter(_.gameId === id)
    .map(mech => (mech.winningboard, mech.versuche))
    .update((obj.winningBoardTojson(), obj.versuche))
    
    val resultFuture = db.run(query)
  }

  //DARF NICHT genutzt werden
  override def delete(id: Long): Unit = {
    val query = mechTable
    .filter(_.mechId === id)
    .delete
    
    val resultFuture = db.run(query)
  }
  
  def winningBoardFromJson(winningboard:String):Map[Int, Boolean]={
    val json: JsValue = Json.parse(winningboard)
    val result: JsResult[Map[Int, Boolean]] = (json \ "winningBoard").validate[Map[Int, Boolean]]
    result.get
  }
}
