package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.ModeData
import model.persistenceComponent.slickComponent.ModeTable
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
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

  override def findById(id: Long): ModeData = ???

  override def update(id: Long, obj: ModeData): Unit = ???

  override def delete(id: Long): Unit = ???
}
