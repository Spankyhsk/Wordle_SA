package model.FileIOComponent

import model.GameInterface

trait FileIOInterface {
  
  def load(game:GameInterface):String
  def save(game: GameInterface):Unit

}
