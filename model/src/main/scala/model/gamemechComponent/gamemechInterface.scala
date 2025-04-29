package model.gamemechComponent

import scala.util.Try

trait gamemechInterface {
  def count( limit:Int):Option[Boolean]
  def controllLength(n:Int, wordLength:Int):Either[Boolean, Boolean]
  def controllRealWord(guess:String):Either[Boolean, Boolean]
  def buildWinningBoard(n:Int, key:Int):gamemechInterface
  def setWin(key:Int):gamemechInterface
  def getWinOption(key:Int):Option[Boolean]
  def areYouWinningSon():Option[Boolean]
  def GuessTransform(guess:String):String
  def compareTargetGuess(n:Int, targetWord:Map[Int, String], guess:String):gamemechInterface
  def evaluateGuess(targetWord:String, guess:String):Try[String]

  def getN(): Int
  def setN(zahl: Integer): gamemechInterface
  def getWinningBoard():Map[Int, Boolean]
  def setWinningBoard(wBoard:Map[Int, Boolean]):gamemechInterface

  def resetWinningBoard(size: Int): gamemechInterface
}
