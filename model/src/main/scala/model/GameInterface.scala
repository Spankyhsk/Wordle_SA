package model


import model.gamemechComponent.gamemechInterface
import model.gamemodeComponnent.GamemodeInterface
import model.gamefieldComponent.GamefieldInterface

trait GameInterface {
  
  //===================================================================
  
              //!!!Mech!!!
  
  //===================================================================

  def getGamemech():gamemechInterface

  def count(): Boolean

  def controllLength(n: Int): Boolean

  def controllRealWord(guess: String): Boolean

  def evaluateGuess(guess: String): Map[Int, String]

  def createwinningboard(): Unit

  def areYouWinningSon(guess: String): Boolean

  def GuessTransform(guess:String):String

  def setWinningboard(wBoard: Map[Int, Boolean]):Unit

  def setN(zahl: Integer): Unit

  def getN():Int
  
  //===================================================================
  
              //!!!Board!!!
  
  //===================================================================

  def getGamefield():GamefieldInterface[GamefieldInterface[String]]

  def createGameboard(): Unit

  def toString(): String
  
  def setMap(boardmap:Map[Int, Map[Int, String]]):Unit

  def setRGameboard(key:Int, feedback: Map[Int, String]):Unit

  def undoStep(key:Int, feedback: Map[Int,String]):Unit
  
  //===================================================================
  
              //!!!Mode!!!
  
  //===================================================================
  
  def getGamemode():GamemodeInterface
  
  def changeState(e: Int): Unit

  def getTargetword(): Map[Int, String]

  def getLimit(): Int
  
  def setTargetWord(targetWordMap: Map[Int, String]): Unit
  def setLimit(Limit: Int): Unit
  
  def TargetwordToString():String

  

}
