package model.gamefieldComponent

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

//=====================================================================

              //!!!GAMEFIELD!!!

//=====================================================================

case class gamefield(var Field: mutable.Map[Int, String] = mutable.Map.empty) extends GamefieldInterface[String] {
  def getMap(): Map[Int, String] = Field.toMap

  def setMap(boardmap: Map[Int, Map[Int, String]]): GamefieldInterface[String] = this

  def SetMapper(field: Map[Int, String]): Unit = Field = mutable.Map(field.toSeq: _*)


  def set(key: Int, feedback: String): Unit = Field = Field.clone().addOne((key, feedback))

  def setR(n: Int, key: Int, feedback: Map[Int, String]): Unit = {}

  def buildGamefield(n: Int, key: Int, value: String): Unit = {
    Field += (key -> value)
    if (key < n) buildGamefield(n, key + 1, value)
  }

  def buildGameboard(n: Int, key: Int): Unit = {}

  override def toString: String = Field.toSeq.sortBy(_._1).map(_._2).mkString("\n")

  override def reset(): Unit = Field.clear()

}

//=====================================================================

                //!!!GAMEBOARD!!!

//=====================================================================

case class gameboard(var Board: mutable.Map[Int, GamefieldInterface[String]] = mutable.Map.empty) extends GamefieldInterface[GamefieldInterface[String]] {
  def getMap(): Map[Int, GamefieldInterface[String]] = Board.toMap

  def setMap(boardmap: Map[Int, Map[Int, String]]): GamefieldInterface[GamefieldInterface[String]] = setMapR(boardmap.size, 1, boardmap)

  def setMapR(n: Int, key: Int, boardmap: Map[Int, Map[Int, String]]): GamefieldInterface[GamefieldInterface[String]] = {
    Try {
      val GameField = new gamefield()
      GameField.SetMapper(boardmap(key))
      Board += (key -> GameField)
      if (key < n) setMapR(n, key + 1, boardmap)
    } match {
      case Success(_) => this
      case Failure(exception) =>
        println(s"Fehler beim Setzen der Map: ${exception.getMessage}")
        Board -= key
        this // Rückgabe des aktuellen Objekts im Fehlerfall
    }
  }

  override def set(key: Int, feedback: String): Unit = {}

  def setR(n: Int, key: Int, feedback: Map[Int, String]): Unit = {
    Board.get(n).foreach { gameField =>
      val feedbackValue = feedback.get(n)
      feedbackValue match {
        case Some(value) => gameField.set(key, value)
        case None => println(s"Kein Feedback für Schlüssel $n gefunden")
      }
    }
    if (n < Board.size) setR(n + 1, key, feedback)
  }

  override def buildGamefield(n: Int, key: Int, value: String): Unit = {}

  def buildGameboard(n: Int, key: Int): Unit = {
    Board += (key -> new gamefield())
    if (key < n) buildGameboard(n, key + 1)
  }

  override def reset(): Unit = Board.clear()

  override def toString: String = {
    val gamefieldsString = Board.toSeq.sortBy(_._1).map { case (_, field) => field.toString }
    gamefieldsString.mkString("\n\n") // Zwei Zeilenumbrüche für die Trennung zwischen den Gamefields
  }
}
  

