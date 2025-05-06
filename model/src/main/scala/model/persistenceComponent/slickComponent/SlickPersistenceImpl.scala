package model.persistenceComponent.slickComponent

import model.GameInterface
import model.gamefieldComponent.GamefieldInterface
import model.persistenceComponent.PersistenceInterface
import model.persistenceComponent.slickComponent.DAO.{BoardDAO, GameDAO, MechDAO, ModeDAO}
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api.*
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import model.persistenceComponent.entity.{BoardData, MechData, ModeData}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}

import scala.util.{Failure, Success}

class SlickPersistenceImpl() extends PersistenceInterface{

  /**
   * Hier muss alles hin für die Datenbank verbindung, setup und weiteres
   * */
  private val logger = org.slf4j.LoggerFactory.getLogger(classOf[SlickPersistenceImpl])

  private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "postgres")
  private val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "postgres")
  private val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "localP4$$wort")
  private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", "5432")
  private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "localhost")
  //private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "host.docker.internal")
  private val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB"

  val database = Database.forURL(
    url = databaseUrl,
    driver = "org.postgresql.Driver",
    user = databaseUser,
    password = databasePassword
  )

  private val gameTable = TableQuery[GameTable]
  private val modeTable = TableQuery[ModeTable]
  private val mechTable = TableQuery[MechTable]
  private val boardTable = TableQuery[BoardTable]

  private val setup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(
    gameTable.schema.createIfNotExists,
    modeTable.schema.createIfNotExists,
    mechTable.schema.createIfNotExists,
    boardTable.schema.createIfNotExists
  )
  println("create Tables")

  database.run(setup).onComplete {
    case Success(value) => logger.info("Tables created")
    case Failure(exception) => logger.error(exception.getMessage)
  }

  def closeConnection = database.close

  def dropTables(): Unit = {
    val shutdownSetup: DBIOAction[Unit, NoStream, Effect.Schema] = DBIO.seq(
      gameTable.schema.dropIfExists,
      modeTable.schema.dropIfExists,
      mechTable.schema.dropIfExists,
      boardTable.schema.dropIfExists
    )
    database.run(shutdownSetup).onComplete {
      case Success(value) => logger.info("Tables dropped")
      case Failure(exception) => logger.error(exception.getMessage)
    }
  }

  override def save(game: GameInterface, name:String): Long = {
    //GameDao
    val gameId = GameDAO(database).save(name)
    ModeDAO(database).save(new ModeData(gameId, game.getGamemode().getTargetword(), game.getGamemode().getLimit()))
    MechDAO(database).save(new MechData(gameId, game.getGamemech().getWinningBoard(), game.getGamemech().getN()))
    BoardDAO(database).save(new BoardData(gameId, gameboardToJason(game.getGamefield().getMap()) ))
    println("Database save Game")
    gameId
  }

  override def load(gameId:Long, game: GameInterface): Unit = {
    val mech = MechDAO(database).findById(gameId)
    mech.winningBoard.size match {
      case 1 =>
        game.changeState(1)
      case 2 =>
        game.changeState(2)
      case 4 =>
        game.changeState(3)
    }
    
    game.setWinningboard(mech.winningBoard)
    game.setN(mech.versuche)
    
    val board = BoardDAO(database).findById(gameId)
    game.setMap(gameboardFromJason(board.boardMap))
    
    val mode = ModeDAO(database).findById(gameId)
    game.setTargetWord(mode.targetWordMap)
    game.setLimit(mode.limit)
  }

  override def search(): String = ???

  def gameboardToJason(gameBoard: Map[Int, GamefieldInterface[String]]): String = {
    Json.prettyPrint(
      Json.obj(
        "gameboard" -> Json.toJson(
          for {
            key <- 1 until gameBoard.size + 1
          } yield {
            Json.obj(
              "key" -> key,
              "gamefield" -> gameBoard(key).getMap()
            )
          }
        )
      ))
  }

  def gameboardFromJason(gameboard: String): Map[Int, Map[Int, String]] = {

    val jsValue = Json.parse(gameboard)

    val gameboardArray = (jsValue \ "gameboard").as[JsArray]

    gameboardArray.value.map { entry =>
      val key = (entry \ "key").as[Int]

      val gamefieldJsObj = (entry \ "gamefield").as[JsObject]
      val gamefieldMap: Map[Int, String] = gamefieldJsObj.fields.map {
        case (k, JsString(v)) => k.toInt -> v
        case (k, v) => throw new RuntimeException(s"Unerwarteter Wert für $k: $v")
      }.toMap

      key -> gamefieldMap
    }.toMap
  }
}
