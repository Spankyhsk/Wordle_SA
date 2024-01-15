
package de.htwg.se.wordle.controller
import de.htwg.se.wordle.util.Observable
import de.htwg.se.wordle.model.GameInterface
import de.htwg.se.wordle.model.FileIOComponent.*
import de.htwg.se.wordle.model.Game
import de.htwg.se.wordle.util.Event

import scala.util.{Failure, Success, Try}
import de.htwg.se.wordle.util.UndoManager

case class controll (game:GameInterface, file:FileIOInterface)extends ControllerInterface with Observable {

  var gamemode = game.getGamemode()
  val gamemech = game.getGamemech()
  val gameboard = game.getGamefield()
  private val undoManager = new UndoManager

  def save():Unit={
    file.save(game)
    notifyObservers(Event.Move)
  }

  def load():Unit={
    file.load(game)
    notifyObservers(Event.Move)
  }

  
  def count(n: Int): Boolean = {
    val continue = game.count(n)
    if(!continue){
      notifyObservers(Event.LOSE)
    }
    continue
  }

  def controllLength(n: Int): Boolean = {
    game.controllLength(n)
  }

  def controllRealWord(guess: String): Boolean = {
    game.controllRealWord(guess)
  }

  def createGameboard(): Unit = {
    game.createGameboard()
  }


  

  def set(key: Int, feedback: Map[Int, String]): Unit = {
    undoManager.doStep(new SetCommand(key, feedback, this))
    notifyObservers(Event.Move)
  }

  def undo(): Unit = {
    undoManager.undoStep
    notifyObservers(Event.UNDO)
  }

 

  def evaluateGuess(guess: String): Map[Int, String] = {
    game.evaluateGuess(guess)
  }

  override def toString: String = {
    game.toString
  }

  def changeState(e: Int): Unit = {
    game.resetGameboard() // Spielbrett zurücksetzen
    game.changeState(e)
    //createGameboard() // Neues Spielbrett initialisieren
    //createwinningboard()
    notifyObservers(Event.NEW)
  }
  
  def resetGameboard():Unit={
    game.resetGameboard()
  }


  def getTargetword(): Map[Int, String] = {
    game.getTargetword()
  }

  def getLimit(): Int = {
    game.getLimit()
  }

  def createwinningboard(): Unit = {
    game.createwinningboard()
    notifyObservers(Event.Move)
  }

  def areYouWinningSon(guess: String): Boolean = {
    val won = game.areYouWinningSon(guess)
    if(won){
      notifyObservers(Event.WIN)
    }
    won
  }

  def GuessTransform(guess: String): String = {
    game.GuessTransform(guess)
  }
  def setVersuche(zahl:Integer):Unit={
    game.setN(zahl)
  }

  def getVersuche():Int={
    game.getN()
  }
  
}

object controll:
  def apply(kind:String):controll ={
    kind match {
      case "XML" => controll(Game("norm"), new FileIOXML)
      case "JSON" => controll(Game("norm"), new FileIOJSON)
    }
  }
