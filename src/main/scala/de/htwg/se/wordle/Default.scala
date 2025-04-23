package de.htwg.se.wordle

import model._
import model.gamefieldComponent.{GamefieldInterface, gameboard}
import model.gamemechComponent.{GameMech, gamemechInterface}
import model.gamemodeComponnent.{GamemodeInterface, gamemode}
import controller.{controll, ControllerInterface}
import com.google.inject.Guice
import model.FileIOComponent.FileIOInterface
import model.FileIOComponent.{FileIOJSON => FileIOJSON}




object Default:

  // Game-Komponenten
  given GameInterface = new Game(new GameMech(), new gameboard(), gamemode(1))
  given GamefieldInterface[GamefieldInterface[String]] = new gameboard()
  given gamemechInterface = new GameMech()
  given GamemodeInterface = gamemode(1)

  // FileIO
  given FileIOInterface = new FileIOJSON()


  // Controller
  val injector = Guice.createInjector(new WordleModuleJson)
  given ControllerInterface = injector.getInstance(classOf[ControllerInterface])

  // Optional: Factory Methoden
  def createGame(): GameInterface = new Game(new GameMech(), new gameboard(), gamemode(1))
  def createGameWithMode(e: Int): GameInterface = new Game(new GameMech(), new gameboard(), gamemode(e))