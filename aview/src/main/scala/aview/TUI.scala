package aview

import controller.ControllerInterface
import util.{Event, Observer}

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



  def scanInput(input: String): Unit ={
    input match
      case "$quit" => {
        println(s"Wiedersehen")
        sys.exit(0)
      }
      case "$undo"=>{
        controllerClient.putUndoMove()
      }
      case "$save"=>{
        println("Spielstand wurde gespeichert")
        controllerClient.postGameSave()
      }
      case "$load"=>{
        println("Spielstand wird geladen")
        val message = controllerClient.getGameSave().toString
        println(message)
      }
      case "$switch"=>{
        newgame = true
      }
      case default =>{
        val guess = controller.GuessTransform(input)
        if(controller.controllLength(guess.length) && controller.controllRealWord(guess)) {
          if (!controller.areYouWinningSon(guess)&&controller.count()) {
            controller.set(controller.getVersuche(), controller.evaluateGuess(guess))
            controller.setVersuche(controller.getVersuche() + 1)
          }else{
            controller.set(controller.getVersuche(), controller.evaluateGuess(guess))
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
        println(controller.toString)
        if(!newgame) {
          println("Dein Tipp: ")
        }
      }
      case Event.NEW=>{
        controller.setVersuche(1)
        newgame = false
        println("Errate Wort:") //guess
      }
      case Event.UNDO=>{
        controller.setVersuche(controller.getVersuche()-1)
        println(controller.toString)
        println("Dein Tipp: ")
      }
      case Event.WIN =>{
        println(s"Du hast gewonnen! Lösung: "+ controller.TargetwordToString())
        newgame = true

      }
      case Event.LOSE =>{
        println(s"Verloren! Versuche aufgebraucht. Lösung: "+ controller.TargetwordToString())
        newgame = true

      }
  }

