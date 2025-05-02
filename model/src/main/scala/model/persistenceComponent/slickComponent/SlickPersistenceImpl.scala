package model.persistenceComponent.slickComponent

import model.GameInterface
import model.persistenceComponent.PersistenceInterface
import model.persistenceComponent.slickComponent.DAO.{BoardDAO, GameDAO, MechDAO, ModeDAO}
import slick.dbio.{DBIO, DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery

import scala.util.{Failure, Success}

class SlickPersistenceImpl(game: GameInterface) extends PersistenceInterface{

  /**
   * Hier muss alles hin für die Datenbank verbindung, setup und weiteres
   * */
  private val logger = org.slf4j.LoggerFactory.getLogger(classOf[SlickPersistenceImpl])

  private val databaseDB: String = sys.env.getOrElse("POSTGRES_DATABASE", "postgres")
  private val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "postgres")
  private val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "Kiiing001")
  private val databasePort: String = sys.env.getOrElse("POSTGRES_PORT", "5432")
  //private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "localhost")
  private val databaseHost: String = sys.env.getOrElse("POSTGRES_HOST", "host.docker.internal")
  private val databaseUrl = s"jdbc:postgresql://$databaseHost:$databasePort/$databaseDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&autoReconnect=true"

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

  override def save(game: GameInterface): Long = {
    //GameDao
    val gameId = GameDAO(database).save("")
    ModeDAO(database).save(new ModeData(gameId, game.getGamemode().getTargetword(), game.getGamemode().getLimit()))
    MechDAO(database).save(new MechData(gameId, game.getGamemech().getWinningBoard(), game.getGamemech().getN()))
    BoardDAO(database).save(new BoardDate(gameId, game.getGamefield().getMap()))
    println("Database save Game")
    gameId
  }

  override def load(gameId:Long, game: GameInterface): Unit = {

  }
}
