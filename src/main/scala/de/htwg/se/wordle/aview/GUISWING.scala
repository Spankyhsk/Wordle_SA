package de.htwg.se.wordle.aview

import de.htwg.se.wordle.controller.ControllerInterface
import de.htwg.se.wordle.util.{Command, ModeSwitchInvoker, Observer}
import de.htwg.se.wordle.util.EasyModeCommand
import de.htwg.se.wordle.util.MediumModeCommand
import de.htwg.se.wordle.util.HardModeCommand

import scala.swing.*
import scala.swing.event.*
import java.awt.Color
import javax.swing.text._

class GUISWING(controller:ControllerInterface) extends Frame with Observer {
  controller.add(this)
  var n = 1
  title = "Wordle"

  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Exit") {
        sys.exit(0)
      })
    }
  }
  //-------------------------------------
  val headlinepanel = new FlowPanel{
    contents += new Label("Wordle"){
      font = new Font("Arial", Font.Bold.id, 24)
      border = Swing.EmptyBorder(0, 0, 10, 0)
    }
  }
  //------------------------------------
  val level = new Label("???")


  val EasymodusButton = new Button("Leicht")
  val MediummodusButton = new Button("Mittel")
  val HardmodusButton = new Button("Schwer")


  val gamemoduspanel1 = new BoxPanel(Orientation.Horizontal){
    contents += new Label("Schwierigkeitsgrad: ")
    contents += level
  }

  val gamemoduspanel2 = new BoxPanel(Orientation.Horizontal){
    contents += EasymodusButton
    contents += MediummodusButton
    contents += HardmodusButton
  }

  val gamemoduspanelMain = new BoxPanel(Orientation.Vertical){
    contents += gamemoduspanel1
    contents += gamemoduspanel2
  }

  val northpanel = new BoxPanel(Orientation.Vertical){
    contents += menuBar
    contents += headlinepanel
    contents += gamemoduspanelMain
  }
  //------------------------------------------------------
  object inputTextField extends TextField {
    columns = 10
    preferredSize = new Dimension(200, 30)
    maximumSize = new Dimension(200, 30)
    enabled = false
  }

  object OutputTextField extends TextArea {
    rows = 20
    columns = 20
    editable = false
    lineWrap = true
    wordWrap = true
    text = controller.toString
    foreground = Color.BLACK
  }

  val InputPanel = new BoxPanel(Orientation.Vertical){
    contents += new Label("Versuch:")
    contents += inputTextField
  }
  InputPanel.xLayoutAlignment = 0.5
  InputPanel.yLayoutAlignment = 0.5
  //--------------------------------------------------

  val OutputPanel = new FlowPanel{
    contents += OutputTextField
  }

  val centerPanel = new BoxPanel(Orientation.Vertical){
    contents += InputPanel
    contents += OutputPanel
  }
  //--------------------
  object newsBoard extends TextArea {
    columns = 20
    rows = 3
    text = "Suche dir ein Spielmodus aus"
    editable = false
    lineWrap = true
    wordWrap = true
  }
  val southPanel = new BorderPanel {
    border = Swing.LineBorder(java.awt.Color.BLACK)
    add(newsBoard, BorderPanel.Position.Center)
  }


  //-------------------------------------
  contents = new BorderPanel{
    add(northpanel, BorderPanel.Position.North)
    add(centerPanel, BorderPanel.Position.Center)
    add(southPanel, BorderPanel.Position.South)
    border = Swing.EmptyBorder(10, 10, 10, 10) // Abstand von 10 Pixeln oben, unten, links und rechts
  }

  val modeSwitchInvoker = new ModeSwitchInvoker()

  listenTo(inputTextField, EasymodusButton, MediummodusButton, HardmodusButton)
  var won = false
  reactions +={
    case EditDone(inputTextField) =>
      val guess = controller.GuessTransform(inputTextField.text)

      if(controller.controllLength(guess.length)){
        controller.set(n, controller.evaluateGuess(guess))
        if(controller.areYouWinningSon(guess)){
          newsBoard.text = "Glückwunsch!! Du hast Gewonnen.\n zum erneuten Spielen Schwierigkeitsgrad aussuchen"
          inputTextField.enabled = false
          //controller.set(n, controller.evaluateGuess(guess))
          won = true
        }
      }else{
        newsBoard.text = "Falsche Eingabe"
        n = n -1
      }
      if (!controller.count(n) && !won) {
        newsBoard.text = "Verloren!\n zum erneuten Spielen Schwierigkeitsgrad aussuchen"
        inputTextField.enabled = false
      } else {
        n = n + 1
      }


    case ButtonClicked(EasymodusButton)=>
      modeSwitchInvoker.setCommand(EasyModeCommand(Some(controller)))
      modeSwitchInvoker.executeCommand()


      controller.changeState(1)
      controller.createGameboard()
      controller.createwinningboard()
      level.text = "leicht"
      newsBoard.text = "Errate 1 Wort mit 1 guess bevor die Versuche ausgehen"
      inputTextField.enabled = true
      n = 1
    case ButtonClicked(MediummodusButton)=>
      modeSwitchInvoker.setCommand(MediumModeCommand(Some(controller)))
      modeSwitchInvoker.executeCommand()


      controller.changeState(2)
      controller.createGameboard()
      controller.createwinningboard()
      level.text = "mittel"
      newsBoard.text = "Errate 2 Wörter mit 1 guess bevor die Versuche ausgehen"
      inputTextField.enabled = true
      n = 1
    case ButtonClicked(HardmodusButton)=>
      modeSwitchInvoker.setCommand(HardModeCommand(Some(controller)))
      modeSwitchInvoker.executeCommand()


      controller.changeState(3)
      controller.createGameboard()
      controller.createwinningboard()
      level.text = "schwer"
      newsBoard.text = "Errate 4 Wörter mit 1 guess bevor die Versuche ausgehen"
      inputTextField.enabled = true
      n = 1
  }
  pack()
  maximumSize = new Dimension(300, 800)
  preferredSize = new Dimension(300,800)
  centerOnScreen()
  open()



  override def update:Unit={
    val filteredAndColoredText = filterAndColor(controller.toString)

    OutputTextField.text = filteredAndColoredText
    OutputTextField.peer.setCaretPosition(0)
    newsBoard.text
    level.text

  }

  def filterAndColor(input: String): String = {
    val YellowPattern = """\u001B\[33m([^\\]+)\u001B\[0m""".r
    val GreenPattern = """\u001B\[32m([^\\]+)\u001B\[0m""".r

    val yellowColored = YellowPattern.replaceAllIn(input, m => colorToANSIEscape(Color.YELLOW) + m.group(1) + "\u001B[0m")
    val greenColored = GreenPattern.replaceAllIn(yellowColored, m => colorToANSIEscape(Color.GREEN) + m.group(1) + "\u001B[0m")

    greenColored
  }

  private def colorToANSIEscape(color: Color): String = {
    val ANSI_COLOR_ESCAPE = "\u001B[38;2;"
    val ANSI_COLOR_RESET = "\u001B[0m"

    val rgb = color.getRGB
    val r = (rgb >> 16) & 0xFF
    val g = (rgb >> 8) & 0xFF
    val b = rgb & 0xFF

    s"$ANSI_COLOR_ESCAPE$r;$g;$b" + "m" + ANSI_COLOR_RESET // ANSI escape sequence for setting text color and resetting
  }

}
