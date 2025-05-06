package model.persistenceComponent

import model.GameInterface

trait PersistenceInterface {
  def save(game:GameInterface, name:String):Long
  def load(gameId:Long ,game:GameInterface):Unit
}
