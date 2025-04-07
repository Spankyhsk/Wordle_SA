package de.htwg.se.wordle.model.gamemechComponent

import scala.collection.mutable
import scala.util.Try

// Klasse für den Spielmechanismus mit Strategie
case class GameMech(
                     guessStrategy: GuessStrategy = new SimpleGuessStrategy,
                     winningBoard: mutable.Map[Int, Boolean] = mutable.Map.empty,
                     private var versuch: Int = 1
                   ) extends gamemechInterface {

  def count(limit: Int): Boolean = versuch < limit

  def controllLength(n: Int, wordLength: Int): Either[Boolean, Boolean] =
    if (n == wordLength) Right(true)    // Erfolg
    else Left(false)                    // Fehler

  def controllRealWord(guess: String): Either[Boolean, Boolean] = 
    if (guess.forall(_.isLetter)) Right(true)
    else Left(false)

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

  def getWinOption(key: Int): Option[Boolean] = winningBoard.get(key)

  def areYouWinningSon(): Boolean = winningBoard.values.forall(_ == true)

  def GuessTransform(guess: String): String = guess.toUpperCase

  def resetWinningBoard(size: Int): GameMech = {
    winningBoard.clear()
    (1 to size).foreach(winningBoard(_) = false)
    this
  }

  def compareTargetGuess(n: Int, targetWord: Map[Int, String], guess: String): GameMech = {
    getWinOption(n) match {
      case Some(false) => // Feld existiert & noch nicht gewonnen
        targetWord.get(n) match {
          case Some(target) =>
            val updatedBoard = guessStrategy.compareTargetGuess(target, guess, n, winningBoard.toMap)
            updatedBoard.foreach { case (key, value) => winningBoard(key) = value }
          case None =>
          // kein target vorhanden — eventuell loggen oder ignorieren
        }
      case _ => // entweder key fehlt oder schon gewonnen → nichts tun
    }

    if (n < winningBoard.size) compareTargetGuess(n + 1, targetWord, guess)
    this
  }

  def evaluateGuess(targetWord: String, guess: String): Try[String] =
    Try(guessStrategy.evaluateGuess(targetWord, guess))

  def getN(): Int = versuch

  def setN(zahl: Integer): GameMech = {
    Option(zahl).foreach(versuch = _)
    this
  }

  def getWinningBoard(): Map[Int, Boolean] = winningBoard.toMap

  def setWinningBoard(wBoard: Map[Int, Boolean]): GameMech = {
    winningBoard.clear()
    winningBoard ++= wBoard
    this
  }
}





  

