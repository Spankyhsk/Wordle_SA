package controller

import util.Observable
import scala.util.{Failure, Success, Try}

trait ControllerInterface extends Observable{

  def startGame():Unit
  //==========================================================
  
              //!!!GAME!!!
  
  //==========================================================
  
  //----------------------------------------------------------
  
              //mech
  
  //----------------------------------------------------------

  def count():Boolean

  def controllLength(n:Int):Boolean

  def controllRealWord(guess:String):Boolean

  def evaluateGuess(guess:String):Map[Int, String]

  def GuessTransform(guess:String):String

  def setVersuche(zahl:Integer):Unit

  def getVersuche():Int

  def areYouWinningSon(guess:String):Boolean

  def createwinningboard():Unit
  
  
  //----------------------------------------------------------
  
              //board
  
  //----------------------------------------------------------

  def createGameboard():Unit

  def toString():String
  
  //----------------------------------------------------------
  
              //mode
  
  //----------------------------------------------------------

  def changeState(e: Int): Unit

  def TargetwordToString():String
  //==========================================================
  
              //!!!undoManger!!!
  
  //==========================================================

  def set(key:Int, feedback:Map[Int, String]):Unit

  def undo():Unit
  
  //==========================================================
  
              //!!!File!!!
  
  //==========================================================
  
  def save(): Unit

  def load():String
  
  //==========================================================
  
            //Persistence
  
  //==========================================================
  
  def putGame(name:String):Unit

  def getGame(gameId:Long):Unit

  def search():String
  
}
