package de.htwg.se.wordle.model.gamemodeComponnent

//====================================================================

              //!!!Gamemode1!!!

//====================================================================
case class gamemode1(
                      wordObject: Word = new Word(),
                      targetword: Map[Int, String] = Map(1 -> new Word().selectRandomWord(new Word().words(5))),
                      limit: Int = 6
                    ) extends GamemodeInterface {
  
  val wordlength: Int = 5

  override def getTargetword(): Map[Int, String] = targetword

  override def getLimit(): Int = limit

  override def getWordList(): Array[String] = wordObject.words(wordlength)

  override def withTargetWord(newTargetWord: Map[Int, String]): gamemode1 =
    copy(targetword = newTargetWord)

  override def withLimit(newLimit: Int): gamemode1 =
    copy(limit = newLimit)

  override def toString(): String =
    targetword.map { case (key, value) => s"Wort$key: $value" }.mkString(" ")
}


//====================================================================

                    //!!!Gamemode2!!!

//====================================================================

case class gamemode2(
                      wordObject: Word = new Word(),
                      targetword: Map[Int, String] = Map(
                        1 -> new Word().selectRandomWord(new Word().words(5)),
                        2 -> new Word().selectRandomWord(new Word().words(5))
                      ),
                      limit: Int = 7
                    ) extends GamemodeInterface {

  val wordlength: Int = 5

  override def getTargetword(): Map[Int, String] = targetword

  override def getLimit(): Int = limit

  override def getWordList(): Array[String] = wordObject.words(wordlength)

  override def withTargetWord(newTargetWord: Map[Int, String]): gamemode2 =
    copy(targetword = newTargetWord)

  override def withLimit(newLimit: Int): gamemode2 =
    copy(limit = newLimit)

  override def toString(): String =
    targetword.map { case (key, value) => s"Wort$key: $value" }.mkString(", ")
}

//====================================================================

                      //!!!Gamemode3!!!

//====================================================================


case class gamemode3(
                      wordObject: Word = new Word(),
                      targetword: Map[Int, String] = Map(
                        1 -> new Word().selectRandomWord(new Word().words(5)),
                        2 -> new Word().selectRandomWord(new Word().words(5)),
                        3 -> new Word().selectRandomWord(new Word().words(5)),
                        4 -> new Word().selectRandomWord(new Word().words(5))
                      ),
                      limit: Int = 8
                    ) extends GamemodeInterface {

  val wordlength: Int = 5

  override def getTargetword(): Map[Int, String] = targetword

  override def getLimit(): Int = limit

  override def getWordList(): Array[String] = wordObject.words(wordlength)

  override def withTargetWord(newTargetWord: Map[Int, String]): gamemode3 =
    copy(targetword = newTargetWord)

  override def withLimit(newLimit: Int): gamemode3 =
    copy(limit = newLimit)

  override def toString(): String =
    targetword.map { case (key, value) => s"Wort$key: $value" }.mkString(", ")
}



object gamemode{
  
  var state: GamemodeInterface = gamemode1()

  def apply(e: Int) = {
    e match {
      case 1 => gamemode1()
      case 2 => gamemode2()
      case 3 => gamemode3()
    }
  }
 

}
