package de.htwg.se.wordle.model.gamemechComponent

trait gamemechInterface {
  def count( limit:Int):Boolean
  def controllLength(n:Int, wordLength:Int):Boolean
  def controllRealWord(guess:String):Boolean
  def buildWinningBoard(n:Int, key:Int):gamemechInterface
  def setWin(key:Int):gamemechInterface
  def getWin(key:Int):Boolean
  def areYouWinningSon():Boolean
  def GuessTransform(guess:String):String
  def compareTargetGuess(n:Int, targetWord:Map[Int, String], guess:String):gamemechInterface
  def evaluateGuess(targetWord:String, guess:String):String

  def getN(): Int
  def setN(zahl: Integer): gamemechInterface
  def getWinningBoard():Map[Int, Boolean]
  def setWinningBoard(wBoard:Map[Int, Boolean]):gamemechInterface

  def resetWinningBoard(size: Int): gamemechInterface
}
