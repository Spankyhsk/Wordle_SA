package aview

controller.add(this)
  var class TUI (controller: ControllerInterface)extends Observer = true

  newgame

  def getnewgame(): Boolean = {
    newgame
  }


  def processInput(input: String): Unit = {
    if(newgame){
      controller.changeState(difficultyLevel(input))
      controller.createGameboard()
      controller.createwinningboard()
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
          controller.undo()
        }
        case "$save"=>{
          println("Spielstand wurde gespeichert")
          controller.save()
        }
        case "$load"=>{
          println("Spielstand wird geladen")
          val message = controller.load()
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





