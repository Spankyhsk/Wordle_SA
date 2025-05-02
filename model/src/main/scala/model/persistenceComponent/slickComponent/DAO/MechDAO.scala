package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.{MechData, ModeData}
import model.persistenceComponent.slickComponent.MechTable
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

class MechDAO(db:Database) extends DAOInterface[MechData, Long]{

  private val mechTable = TableQuery(MechTable(_))

  def save(obj: MechData): Long={
    val mech = (mechTable returning mechTable.map(_.id)) += (
      None,
      obj.winningBoardTojson(),
      obj.versuche,
      obj.gameId
    )
    val resultFuture = db.run(mech)
    mech
  }

  def findAll(): Seq[MechData]

  def findById(id: Long): MechData

  def update(id: Long, obj: MechData): Unit

  def delete(id: Long): Unit
}
