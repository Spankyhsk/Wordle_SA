package model.gamemodeComponnent

trait GamemodeInterface {

  def getTargetword(): Map[Int, String]

  def getLimit(): Int

  def getWordList(): Array[String]

  def withTargetWord(newTargetWord: Map[Int, String]): GamemodeInterface

  def withLimit(newLimit: Int): GamemodeInterface
  
  def toString():String
}
