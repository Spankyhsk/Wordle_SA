package de.htwg.se.wordle


import com.google.inject.Guice
import aview.{ControllerClient, GUISWING, TUI, UIApi}
import controller.ControllerInterface

import scala.io.StdIn.readLine

object wordle {
  def main(args:Array[String]): Unit = {
    val injector = Guice.createInjector(new WordleModuleJson)
    val controll = injector.getInstance(classOf[ControllerInterface])
    val tui = new TUI(new ControllerClient("http://localhost:8081"))
    val gui = new GUISWING(new ControllerClient("http://localhost:8081"))
    
    UIApi(tui, gui)


    println("Willkommen zu Wordle")
    println("Befehle")
    println("$quit := Spiel beenden, $save := Speichern, $load := Laden, $switch := Schwierigkeit verändern")
    while(true){
      if (tui.getnewgame()) {
        println("Gamemode aussuchen: \n1:= leicht\n2:= mittel\n3:= schwer")
      }
      tui.processInput(readLine())
    }

  }
  
}

