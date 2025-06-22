
package controller

import model.FileIOComponent.{FileIOInterface, FileIOJSON, FileIOXML}
import model.{Game, GameInterface}
import util.Event.{Move, NEW, WIN}
import util.{Event, Observable, UndoManager}

case class controll (gameClient:GameClient, fileClient:FileIOClient, observerClient:ObserverClient, persistenceClient: PersistenceClient)extends ControllerInterface with Observable {


  def startGame():Unit ={
    observerClient.triggerEvent(NEW)
    //observerClient.triggerEvent(Move)
  }
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
      observerClient.triggerEvent(Event.LOSE)
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
    println(s"Received guess in controll: $guess")
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
      observerClient.triggerEvent(WIN)
    }
    won
  }

  def createwinningboard(): Unit = {
    gameClient.createWinningBoard()
    println("createWinningboard wird getriggert")
//    notifyObservers(Event.Move)
    //observerClient.triggerEvent(Move)
  }

  //----------------------------------------------------------------------------

          //board

  //----------------------------------------------------------------------------


  def createGameboard(): Unit = {
    gameClient.createGameboard()
    println(s"gameboard: ${gameClient.gameToString}")
  }

  override def toString: String = {
    gameClient.gameToString
  }

  //----------------------------------------------------------------------------

          //Mode

  //----------------------------------------------------------------------------


  def changeState(e: Int): Unit = {
    gameClient.changeState(e)
    //notifyObservers(Event.NEW)
    //observerClient.triggerEvent(NEW)
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
    observerClient.triggerEvent(Event.Move)
  }

  def undo(): Unit = {
    undoManager.undoStep
    notifyObservers(Event.UNDO)
    observerClient.triggerEvent(Event.UNDO)
  }

  //=============================================================================

        //!!!File!!!

  //=============================================================================

  def save():Unit={
    fileClient.save()
    notifyObservers(Event.Move)
    observerClient.triggerEvent(Event.Move)
  }

  def load():String={
    val message = fileClient.load()
    notifyObservers(Event.Move)
    observerClient.triggerEvent(Event.Move)
    message
  }

  def getGame(gameId: Long): Unit = {
    val message = persistenceClient.getGame(gameId)
    notifyObservers(Event.Move)
    observerClient.triggerEvent(Event.Move)
    message
  }

  def search(): String = {
    val message = persistenceClient.search()
    notifyObservers(Event.Move)
    observerClient.triggerEvent(Event.Move)
    message
  }

  //============================================================================

      //Persistence

  //============================================================================

  def putGame(name:String):Unit={
    persistenceClient.putGame(name)
    observerClient.triggerEvent(Event.Move)
  }

  //AkkaKafkaControllerConsumer.consume()
}


