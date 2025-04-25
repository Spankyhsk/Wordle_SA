
package controller

import model.FileIOComponent.{FileIOInterface, FileIOJSON, FileIOXML}
import model.{Game, GameInterface}
import util.{Event, Observable, UndoManager}

case class controll (gameClient:GameClient, fileClient:FileIOClient)extends ControllerInterface with Observable {


  //============================================================================

            //!!!GAME!!!

  //=============================================================================
  def step(key:Int, feedback:Map[Int, String]):Unit={
    gameClient.step(key, feedback)
  }
  
  def undoStep(key:Int, feedback:Map[Int,String]):Unit={
    gameClient.undoStep(key, feedback)
  }

  //-----------------------------------------------------------------------------

          //mech

  //-----------------------------------------------------------------------------

  def count(): Boolean = {
    val continue = gameClient.count()
    if (!continue) {
      notifyObservers(Event.LOSE)
    }
    continue
  }

  def controllLength(n: Int): Boolean = {
    gameClient.controllLength(n)
  }

  def controllRealWord(guess: String): Boolean = {
    gameClient.controllRealWord(guess)
  }

  def evaluateGuess(guess: String): Map[Int, String] = {
    gameClient.evaluateGuess(guess)
  }

  def GuessTransform(guess: String): String = {
    gameClient.guessTransform(guess)
  }

  def setVersuche(zahl: Integer): Unit = {
    gameClient.setVersuche(zahl)
  }

  def getVersuche(): Int = {
    gameClient.getVersuche()
  }

  def areYouWinningSon(guess: String): Boolean = {
    val won = gameClient.areYouWinningSon(guess)
    if (won) {
      notifyObservers(Event.WIN)
    }
    won
  }

  def createwinningboard(): Unit = {
    gameClient.createWinningBoard()
    notifyObservers(Event.Move)
  }

  //----------------------------------------------------------------------------

          //board

  //----------------------------------------------------------------------------


  def createGameboard(): Unit = {
    gameClient.createGameboard()
  }

  override def toString: String = {
    gameClient.gameToString
  }

  //----------------------------------------------------------------------------

          //Mode

  //----------------------------------------------------------------------------


  def changeState(e: Int): Unit = {
    gameClient.changeState(e)
    notifyObservers(Event.NEW)
  }


  def TargetwordToString():String ={
    gameClient.targetWordToString()
  }

  //=============================================================================

          //!!!undoManger!!!

  //=============================================================================

  private val undoManager = new UndoManager

  def set(key: Int, feedback: Map[Int, String]): Unit = {
    undoManager.doStep(new SetCommand(key, feedback, this))
    notifyObservers(Event.Move)
  }

  def undo(): Unit = {
    undoManager.undoStep
    notifyObservers(Event.UNDO)
  }

  //=============================================================================

        //!!!File!!!

  //=============================================================================

  def save():Unit={
    fileClient.save()
    notifyObservers(Event.Move)
  }

  def load():String={
    val message = fileClient.load()
    notifyObservers(Event.Move)
    message
  }

}

//object controll:
//  def apply(kind:String):controll ={
//    kind match {
//      case "XML" => controll(Game("norm"), new FileIOXML)
//      case "JSON" => controll(Game("norm"), new FileIOJSON)
//    }
//  }
