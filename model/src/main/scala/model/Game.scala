package model

case class Game(mech:gamemechInterface, board:GamefieldInterface[GamefieldInterface[String]],var mode:GamemodeInterface)extends GameInterface{
  def this() = this(new GameMech, new gameboard(), gamemode(1))

  //===========================================================================

              //!!!Mech!!!

  //===========================================================================

  def getGamemech(): gamemechInterface = {
    mech
  }

  def count(): Boolean = {
    mech.count(mode.getLimit()) match{
      case Some(true) => true
      case Some(false) => false
      case None => throw new IllegalStateException("Limit ist negativ")
    }
  }

  def controllLength(n: Int): Boolean = {
    val result = mech.controllLength(n, mode.getTargetword()(1).length())
    result match{
      case Right(value) => value
      case Left(error) =>
        println("Falsche Wort Länge")
        error
    }
  }

  def controllRealWord(guess: String): Boolean = {
    val result = mech.controllRealWord(guess)
    result match{
      case Right(value) => value
      case Left(error) =>
        println("Ungültiges Wort oder Zeichen")
        error
    }
  }

  def evaluateGuess(guess: String): Map[Int, String] = {
    // Verwende getOrElse, um das Targetwort zu holen
    getTargetword().keys.toList.sorted.map { key =>
      key -> mech.evaluateGuess(getTargetword()(key), guess).getOrElse("FEHLER")
    }.toMap
  }

  def createwinningboard(): Unit = {
    mech.buildWinningBoard(board.getMap().size, 1)
  }

  def areYouWinningSon(guess: String): Boolean = {
    mech.compareTargetGuess(1, getTargetword(), guess) //??
    mech.areYouWinningSon() match{
      case Some(true) => true
      case Some(false) => false
      case None => throw new IllegalStateException("Gewinnstatus ist nicht verfügbar.")
    }
  }

  def GuessTransform(guess: String): String = {
    mech.GuessTransform(guess)
  }

  def setWinningboard(wBoard: Map[Int, Boolean]) = {
    mech.setWinningBoard(wBoard)
  }

  def setN(zahl: Integer): Unit = {
    mech.setN(zahl)
  }

  def getN(): Int = {
    mech.getN()
  }

  //===========================================================================

            //!!!Board!!!

  //===========================================================================

  def getGamefield(): GamefieldInterface[GamefieldInterface[String]] = {
    board
  }

  def createGameboard(): Unit = {
    board.buildGameboard(mode.getTargetword().size, 1)
    createGamefieldR(1)
  }

  def createGamefieldR(n: Int): Unit = {
    board.getMap()(n).buildGamefield(getLimit(), 1, s"?" * getTargetword()(1).length)
    if (n < board.getMap().size) createGamefieldR(n + 1)
  }

  override def toString(): String = {
    board.toString
  }

  def resetGameboard(): Unit = {
    board.reset()
  }

  def setMap(boardmap: Map[Int, Map[Int, String]]): Unit = {
    board.setMap(boardmap)
  }

  //===========================================================================

            //!!!Mode!!!

  //===========================================================================

  def getGamemode(): GamemodeInterface ={
    mode
  }

  def changeState(e: Int): Unit = {
    mode = gamemode(e)
    mech.resetWinningBoard(mode.getTargetword().size)
    resetGameboard()
  }

  def getTargetword(): Map[Int, String] = {
    mode.getTargetword()
  }

  def getLimit(): Int = {
    mode.getLimit()
  }

  def setTargetWord(targetWordMap: Map[Int, String]): Unit={
    mode.withTargetWord(targetWordMap)
  }

  def setLimit(Limit: Int): Unit={
    mode.withLimit(Limit)
  }
  def TargetwordToString():String={
    mode.toString()
  }


}
