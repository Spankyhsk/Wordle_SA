package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.{MechData, ModeData}
import model.persistenceComponent.slickComponent.MechTable
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

  override def findAll(): Seq[MechData] = ???

  override def findById(id: Long): MechData = ???

  override def update(id: Long, obj: MechData): Unit = ???

  override def delete(id: Long): Unit = ???
}
