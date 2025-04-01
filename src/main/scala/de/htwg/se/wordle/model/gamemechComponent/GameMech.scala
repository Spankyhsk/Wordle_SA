package de.htwg.se.wordle.model.gamemechComponent

import scala.collection.mutable

// Klasse für den Spielmechanismus mit Strategie
case class GameMech(
                     guessStrategy: GuessStrategy = new SimpleGuessStrategy,
                     winningBoard: mutable.Map[Int, Boolean] = mutable.Map.empty,
                     private var versuch: Int = 1
                   ) extends gamemechInterface {

  def count(limit: Int): Boolean = versuch < limit

  def controllLength(n: Int, wordLength: Int): Boolean = n == wordLength

  def controllRealWord(guess: String): Boolean = guess.forall(_.isLetter)

  def buildWinningBoard(n: Int, key: Int): GameMech = {
    if (key <= n) {
      winningBoard(key) = false
      buildWinningBoard(n, key + 1)
    }
    this
  }

  def setWin(key: Int): GameMech = {
    winningBoard(key) = true
    this
  }

  def getWin(key: Int): Boolean = winningBoard.getOrElse(key, false)

  def areYouWinningSon(): Boolean = winningBoard.values.forall(_ == true)

  def GuessTransform(guess: String): String = guess.toUpperCase

  def resetWinningBoard(size: Int): GameMech = {
    winningBoard.clear()
    (1 to size).foreach(winningBoard(_) = false)
    this
  }

  def compareTargetGuess(n: Int, targetWord: Map[Int, String], guess: String): GameMech = {
    if (winningBoard.contains(n) && !getWin(n)) {
      val updatedBoard = guessStrategy.compareTargetGuess(targetWord(n), guess, n, winningBoard.toMap)
      updatedBoard.foreach { case (key, value) => winningBoard(key) = value }
      if (n < winningBoard.size)
        compareTargetGuess(n + 1, targetWord, guess)
    } else {
      if (n < winningBoard.size)
        compareTargetGuess(n + 1, targetWord, guess)
    }
    this
  }

  def evaluateGuess(targetWord: String, guess: String): String =
    guessStrategy.evaluateGuess(targetWord, guess)

  def getN(): Int = versuch

  def setN(zahl: Integer): GameMech = {
    versuch = zahl
    this
  }

  def getWinningBoard(): Map[Int, Boolean] = winningBoard.toMap

  def setWinningBoard(wBoard: Map[Int, Boolean]): GameMech = {
    winningBoard.clear()
    winningBoard ++= wBoard
    this
  }
}





  

