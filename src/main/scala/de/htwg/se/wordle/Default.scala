package de.htwg.se.wordle

import model.*
import model.gamefieldComponent.{GamefieldInterface, gameboard}
import model.gamemechComponent.{GameMech, gamemechInterface}
import model.gamemodeComponnent.{GamemodeInterface, gamemode}
import controller.{ControllerInterface, FileIOClient, GameClient, ObserverClient, controll}
import com.google.inject.Guice
import model.FileIOComponent.FileIOInterface
import model.FileIOComponent.FileIOJSON




object Default:

  // Game-Komponenten
  given GameInterface = Game("norm")
//  given GamefieldInterface[GamefieldInterface[String]] = new gameboard()
//  given gamemechInterface = new GameMech()
//  given GamemodeInterface = gamemode(1)

  // FileIO
  given FileIOInterface = new FileIOJSON()


  // Controller
//  val injector = Guice.createInjector(new WordleModuleJson)
//  given ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  given ControllerInterface = new controll(new GameClient("http://model-service:8082/model/game"), new FileIOClient("http://model-service:8082/model/fileIO"), new ObserverClient("http://ui-service:8080/ui"))

  // Optional: Factory Methoden
//  def createGame(): GameInterface = new Game(new GameMech(), new gameboard(), gamemode(1))
//  def createGameWithMode(e: Int): GameInterface = new Game(new GameMech(), new gameboard(), gamemode(e))