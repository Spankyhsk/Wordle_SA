package model.persistenceComponent

import model.GameInterface

trait PersistenceInterface {
  def save(game:GameInterface):Long
  def load(gameId:Long ,game:GameInterface):Unit
}
