package aview

import controller.ControllerInterface
import util.{Event, Observer}

import scala.io.StdIn
import scala.util.{Failure, Success, Try}


class TUI (controllerClient: ControllerClient)extends Observer:
  
  var newgame = true

  def getnewgame(): Boolean = {
    newgame
  }

  def processInput(input: String): Unit = {
    if(newgame){
      controllerClient.patchChangeState(difficultyLevel(input))
      controllerClient.putCreateGameboard()
      controllerClient.putCreateWinningBoard()
    }else{
      scanInput(input)
    }
  }


  def difficultyLevel(input: String): Int = {
    Try(input.toInt) match {
      case Success(level) if level > 0 && level < 4 =>
        level
      case Success(_) =>
        println("Falsche Angabe, es wird Level Einfach angefangen")
        1
      case Failure(_) =>
        println("Falsche Angabe, es wird Level Einfach angefangen")
        1
    }
  }
  
  def saveGame(name:String): Unit = {
    println("Spielstand wurde online gespeichert")
    controllerClient.putGame(name)
  }



  def scanInput(input: String): Unit ={
    input match
      case "$quit" => {
        println(s"Wiedersehen")
        sys.exit(0)
      }
      case "$undo"=>{
        controllerClient.putUndoMove()
      }
      case "$lokalsave"=>{
        println("Spielstand wurde lokal gespeichert")
        controllerClient.postGameSave()
      }
      case "$SaveDB"=>{
        println("Gib Namen ein:")
        val name = Option(StdIn.readLine()).getOrElse("Unbekannt")
        saveGame(name)
      }
      case "LoadDB"=>{
        println("Gib die ID ein:")
        val gameID = Option(StdIn.readLine()).getOrElse("Unbekannt").toLong
        controllerClient.getGame(gameID)
      }
      case "$load"=>{
        println("Spielstand wird geladen")
        val message = controllerClient.getGameSave().toString
        println(message)
      }
      case "$search"=>{
        val message = controllerClient.search().toString
        println(message)
      }
      case "$switch"=>{
        newgame = true
      }
      case default =>{
        val guess = controllerClient.getGuessTransform(input)
        if(controllerClient.getControllLength(guess.length) && controllerClient.getControllRealWord(guess)) {
          if (!controllerClient.getAreYouWinningSon(guess)&& controllerClient.getCount()) {
            controllerClient.putMove(controllerClient.getVersuche(), controllerClient.getEvaluateGuess(guess))
            controllerClient.putVersuche(controllerClient.getVersuche() + 1)
          }else{
            controllerClient.putMove(controllerClient.getVersuche(), controllerClient.getEvaluateGuess(guess))
          }
        }else{
          println("Falsche Eingabe")
          println("Dein Tipp:")
        }

      }
  }
  override def update(e:Event):Unit = {
    e match
      case Event.Move=> {
        println(controllerClient.getGameBoard())
        if(!newgame) {
          println("Dein Tipp: ")
        }
      }
      case Event.NEW=>{
        controllerClient.putVersuche(1)
        newgame = false
        println("Errate Wort:") //guess
      }
      case Event.UNDO=>{
        controllerClient.putVersuche(controllerClient.getVersuche()-1)
        println(controllerClient.getGameBoard())
        println("Dein Tipp: ")
      }
      case Event.WIN =>{
        println(s"Du hast gewonnen! Lösung: "+ controllerClient.getTargetwordString())
        newgame = true

      }
      case Event.LOSE =>{
        println(s"Verloren! Versuche aufgebraucht. Lösung: "+ controllerClient.getTargetwordString())
        newgame = true

      }
  }

