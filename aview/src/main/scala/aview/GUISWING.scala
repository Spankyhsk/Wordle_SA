package aview

import controller.ControllerInterface
import util.{Event, Observer}

import java.awt.event.{ComponentAdapter, ComponentEvent}
import java.awt.{BorderLayout, Color, Font, GridBagConstraints, GridBagLayout}
import javax.swing.{ImageIcon, JPanel, JScrollPane, SwingUtilities}
import scala.swing.*
import scala.swing.event.{ButtonClicked, EditDone}


class GUISWING(controllerClient: ControllerClient) extends Frame with Observer {

  // Fenster-Titel und Skalierbarkeit
  title = "Wordle"
  resizable = true

//=========================================================================

              //!!!Schriften!!!

//=========================================================================

  // Schriftart definieren
  val customFont = new Font("Skia", Font.PLAIN, 14)
  val comicFont: Font = LoadCustomFont.loadFont("texturengui/font/Comicmeneu-Regular.ttf").deriveFont(24F)


//========================================================================

            //!!!UPDATE!!!

//========================================================================

  override def update(e:Event):Unit={
    e match
      case Event.Move =>{
        upgradeOutput()
        if(editDoneEventFired) {
          NEWSPanel.updateNewsBoardText("Finde das versteckte Wort! Gib dein Tipp ab...")
        }
      }
      case Event.NEW =>{
        controllerClient.putVersuche(1)
        inputTextField.enabled = true
        editDoneEventFired = true
      }
      case Event.WIN =>{
        NEWSPanel.updateNewsBoardText(s"Gewonnen! Lösung: ${controllerClient.getTargetwordString()}\n Zum erneuten Spielen Schwierigkeitsgrad aussuchen")
        inputTextField.enabled = false
        editDoneEventFired = false
      }
      case Event.LOSE =>{
        NEWSPanel.updateNewsBoardText(s"Verloren! Lösung:  ${controllerClient.getTargetwordString()}\n Zum erneuten Spielen Schwierigkeitsgrad aussuchen")
        inputTextField.enabled = false
        editDoneEventFired = false

      }
      case Event.UNDO => {
        upgradeOutput()
        NEWSPanel.updateNewsBoardText("Finde das versteckte Wort! Gib dein Tipp ab...")
      }

  }

//=======================================================================================

                //!!!PANELS!!!

//=======================================================================================


//---------------------------------------------------------------------------------------

            //!!!NorthPanel!!!

//---------------------------------------------------------------------------------------

  menuBar = new MenuBar {
    contents += new Menu("Menü") {
      font = comicFont.deriveFont(12)
      contents += new MenuItem(Action("Exit") {
        sys.exit(0)
      }) {
        font = comicFont.deriveFont(12)
      }
      contents += new MenuItem(Action("Save") {
        controllerClient.postGameSave()
      }) {
        font = comicFont.deriveFont(12)
      }
      contents += new MenuItem(Action("Load") {
        controllerClient.getGameSave()
      }) {
        font = comicFont.deriveFont(12)
      }
    }

  }

  peer.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent): Unit = {
      headlinepanel.updateBannerSize(peer.getSize().width)
    }
  })


  val headlinepanel = new ResizableBannerPanel("texturengui/Wordlebanner2.png")
  headlinepanel.peer.setOpaque(false)
  peer.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent): Unit = {
      headlinepanel.updateBannerSize(peer.getSize().width)
    }
  })


  val EasymodusButton = new TransparentButton("Leicht")
  val MediummodusButton = new TransparentButton("Mittel")
  val HardmodusButton = new TransparentButton("Schwer")


  val gamemoduspanel = new BoxPanel(Orientation.Horizontal) {
    contents += EasymodusButton
    contents += Swing.HStrut(20) // Fügt einen horizontalen Abstand von 10 Pixeln hinzu
    contents += MediummodusButton
    contents += Swing.HStrut(20) // Fügt einen weiteren horizontalen Abstand von 10 Pixeln hinzu
    contents += HardmodusButton
    peer.setOpaque(false)
  }


  val gamemoduspanelMain = new BoxPanel(Orientation.Vertical) { //Methode
    contents += gamemoduspanel
    peer.setOpaque(false)
  }

  var northpanel = new BoxPanel(Orientation.Vertical) {
    contents += menuBar
    contents += headlinepanel
    contents += gamemoduspanelMain
    peer.setOpaque(false)
  }

//---------------------------------------------------------------------------------------

            //!!!CENTERPANEL!!!

//----------------------------------------------------------------------------------------


  val inputImagePath = "texturengui/eingabepaper2.png"
  val originalIcon = new ImageIcon(inputImagePath)

  // Skalieren des Bildes
  var scaledImage = originalIcon.getImage.getScaledInstance(
    170, // Breite anpassen
    70, // Höhe anpassen
    java.awt.Image.SCALE_SMOOTH
  )
  val inputImageIcon = new ImageIcon(scaledImage)


  val inputImageLabel = new Label {
    icon = inputImageIcon
    xLayoutAlignment = 0.5
    yLayoutAlignment = 0.5
  }


  val InputPanel = new Panel {
    peer.setLayout(new GridBagLayout())
    val c = new GridBagConstraints()

    // Einstellungen für das Textfeld
    c.gridx = 0
    c.gridy = 0
    c.weightx = 0
    c.weighty = 0
    c.anchor = GridBagConstraints.CENTER
    c.insets = new Insets(25, 10, 0, 0) // Feinabstimmung der Position
    c.ipadx = -20 // Interne Polsterung
    c.ipady = 20 // Höhe des Textfelds
    peer.add(inputTextField.peer, c) // Korrekte Verwendung von inputTextField

    // Einstellungen für das Bild
    c.gridy = 1
    c.insets = new Insets(-68, 10, 0, 0)
    peer.add(inputImageLabel.peer, c)
    border = Swing.EmptyBorder(0, 0, 0, 0)
    opaque = false
  }

  val OutputPanel = new BoxPanel(Orientation.Vertical) {
    contents += FieldPanel.GameFieldPanel()
    peer.setOpaque(false)
  } //Klappt

  val centerPanelSize = new Dimension(300, 300) // Festgelegte Größe für das centerPanel

  val scrollPane = new ScrollPane(OutputPanel) {
    border = Swing.EmptyBorder(0, 0, 0, 0)
    override lazy val peer: JScrollPane = new JScrollPane(OutputPanel.peer) with SuperMixin {
      setOpaque(false)
      getViewport.setOpaque(false)
    }

  }
  def getScrollPane: ScrollPane = scrollPane

  val centerPanel = new BoxPanel(Orientation.Vertical) {

    peer.setLayout(new GridBagLayout())
    peer.setOpaque(false)

    // Konfiguration für das Eingabefeld
    val c = new GridBagConstraints()
    c.gridx = 0
    c.gridy = 0
    c.gridwidth = 1
    c.anchor = GridBagConstraints.NORTH
    c.weightx = 0.5
    c.weighty = 0.0
    peer.add(InputPanel.peer, c)

    // Konfiguration für das OutputPanel
    c.gridy = 1
    c.weighty = 1.0 // Das restliche Gewicht wird dem OutputPanel zugewiesen
    c.fill = GridBagConstraints.BOTH

    peer.add(scrollPane.peer, c)
  }

//----------------------------------------------------------------------------------------

            //!!!SouthPANEL!!!

//----------------------------------------------------------------------------------------

  val texturedBackground = new TexturedBackground("texturengui/4rippedpaperneu.png") {
    layout(NEWSPanel.NewsBoardPanel()) = BorderPanel.Position.Center
    border = Swing.EmptyBorder(0, 0, 0, 0) // Keine sichtbare Grenze
  }

  val southPanel = new BoxPanel(Orientation.Vertical) {
    border = Swing.LineBorder(java.awt.Color.BLACK)
    contents += texturedBackground
    border = Swing.EmptyBorder(0, 0, 0, 0) // Keine sichtbare Grenze
    peer.setOpaque(false)
  }

//----------------------------------------------------------------------------------------

            //!!!MainPanel!!!

//----------------------------------------------------------------------------------------

  val backgroundPanel = new BackgroundPanel("texturengui/7background.jpg")

  backgroundPanel.setLayout(new BorderLayout())
  backgroundPanel.add(northpanel.peer, BorderLayout.NORTH)
  backgroundPanel.add(centerPanel.peer, BorderLayout.CENTER)
  backgroundPanel.add(southPanel.peer, BorderLayout.SOUTH)

  val scalaBackgroundPanel = new Panel {
    override lazy val peer: JPanel = backgroundPanel
  }

  this.contents = scalaBackgroundPanel

  pack()
  centerOnScreen()
  open()
  peer.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH)// Fenster maximieren


//=======================================================================================

  //!!!REACTION!!!

//=======================================================================================

  var editDoneEventFired = false
  listenTo(inputTextField, EasymodusButton, MediummodusButton, HardmodusButton)
  reactions += {
    case EditDone(inputTextField) =>
      val guess = controllerClient.getGuessTransform(inputTextField.text)
      if (controllerClient.getControllLength(guess.length) && controllerClient.getControllRealWord(guess)) {
        if (!controllerClient.getAreYouWinningSon(guess) && controllerClient.getCount()) {
          controllerClient.putMove(controllerClient.getVersuche(), controllerClient.getEvaluateGuess(guess))
          controllerClient.putVersuche(controllerClient.getVersuche() + 1)
        } else {
          controllerClient.putMove(controllerClient.getVersuche(), controllerClient.getEvaluateGuess(guess))
        }
        } else {
          NEWSPanel.updateNewsBoardText("Falsche Eingabe")
        }

      resetInputField()


    case ButtonClicked(EasymodusButton) =>

      upgradegamemoduspanel(EasymodusButton)
      controllerClient.patchChangeState(1)
      controllerClient.putCreateGameboard()
      controllerClient.putCreateWinningBoard()

    case ButtonClicked(MediummodusButton) =>

      upgradegamemoduspanel(MediummodusButton)
      controllerClient.patchChangeState(2)
      controllerClient.putCreateGameboard()
      controllerClient.putCreateWinningBoard()

    case ButtonClicked(HardmodusButton) =>

      upgradegamemoduspanel(HardmodusButton)
      controllerClient.patchChangeState(3)
      controllerClient.putCreateGameboard()
      controllerClient.putCreateWinningBoard()

  }

//========================================================================================

            //!!!Kleinere Methoden!!!

//========================================================================================

  def upgradegamemoduspanel(clickedButton: Button): Unit = {
    updateButtonColors(clickedButton: Button)
    gamemoduspanelMain.contents.clear()
    gamemoduspanelMain.contents += gamemoduspanel
    gamemoduspanelMain.revalidate()
    gamemoduspanelMain.repaint()
  }

  def updateButtonColors(clickedButton: Button): Unit = {
    val greenColor = new Color(0, 128, 0) // HTML-Grün

    val buttons = List(EasymodusButton, MediummodusButton, HardmodusButton)
    buttons.foreach { button =>
      if (button == clickedButton) {
        button.foreground = greenColor
      } else {
        button.foreground = Color.BLACK
      }
    }
  }

  def resetInputField(): Unit = {
    javax.swing.SwingUtilities.invokeLater(new Runnable {
      def run(): Unit = {
        inputTextField.text = ""
      }
    })
  }

  def upgradeOutput(): Unit = {
    SwingUtilities.invokeLater(new Runnable {
      def run(): Unit = {
        // Speichern der aktuellen Scroll-Position
        val scrollPos = scrollPane.verticalScrollBar.value

        // Aktualisieren des OutputPanel
        val currentGameState = controllerClient.getGameBoard()
        FieldPanel.updateFieldPanel(currentGameState)
        OutputPanel.contents.clear()
        OutputPanel.contents += FieldPanel.GameFieldPanel()
        OutputPanel.revalidate()
        OutputPanel.repaint()

        // Wiederherstellen der Scroll-Position nach dem Repaint/Revalidate
        SwingUtilities.invokeLater(new Runnable {
          def run(): Unit = {
            scrollPane.verticalScrollBar.value = scrollPos
          }
        })
      }
    })
  }
  
}

