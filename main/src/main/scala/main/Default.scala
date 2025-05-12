package main

//import com.google.inject.Guice
import controller.*
import model.*
import model.FileIOComponent.{FileIOInterface, FileIOJSON}
import model.gamefieldComponent.{GamefieldInterface, gameboard}
import model.gamemechComponent.{GameMech, gamemechInterface}
import model.gamemodeComponnent.{GamemodeInterface, gamemode}
import model.persistenceComponent.PersistenceInterface
//import model.persistenceComponent.slickComponent.SlickPersistenceImpl
import model.persistenceComponent.mongodbComponent.MongoDBPersistenceImpl




object Default:

  // Game-Komponenten
  given GameInterface = Game("norm")
//  given GamefieldInterface[GamefieldInterface[String]] = new gameboard()
//  given gamemechInterface = new GameMech()
//  given GamemodeInterface = gamemode(1)

  // FileIO
  given FileIOInterface = new FileIOJSON()
  
//  given PersistenceInterface = new SlickPersistenceImpl()
  given PersistenceInterface = new MongoDBPersistenceImpl()


  // Controller
//  val injector = Guice.createInjector(new WordleModuleJson)
//  given ControllerInterface = injector.getInstance(classOf[ControllerInterface])
  given ControllerInterface = new controll(
    new GameClient(sys.env.getOrElse("MODEL_URL", "http://localhost:8082") + "/model/game"),
    new FileIOClient(sys.env.getOrElse("MODEL_URL", "http://localhost:8082") + "/model/fileIO"),
    new ObserverClient(sys.env.getOrElse("AVIEW_URL", "http://localhost:8080") + "/ui"),
    new PersistenceClient(sys.env.getOrElse("MODEL_URL", "http://localhost:8082")+ "/model/persistence")
  )

  // Optional: Factory Methoden
//  def createGame(): GameInterface = new Game(new GameMech(), new gameboard(), gamemode(1))
//  def createGameWithMode(e: Int): GameInterface = new Game(new GameMech(), new gameboard(), gamemode(e))