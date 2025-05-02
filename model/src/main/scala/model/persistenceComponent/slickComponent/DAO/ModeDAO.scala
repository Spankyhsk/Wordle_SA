package model.persistenceComponent.slickComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.ModeData
import model.persistenceComponent.slickComponent.ModeTable
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

class ModeDAO(db:Database) extends DAOInterface[ModeData, Long]{

  private val modeTable = TableQuery(ModeTable(_))

  def save(obj: ModeData):Long={
    val mode = (modeTable returning modeTable.map(_.id)) +=(
      None,
      obj.targetWordMapToJson(),
      obj.limit,
      obj.gameId
    )
    val resultFuture = db.run(mode)

    mode
  }

  def findAll(): Seq[ModeData]

  def findById(id: Long): ModeData

  def update(id: Long, obj: ModeData): Unit

  def delete(id: Long): Unit
}
