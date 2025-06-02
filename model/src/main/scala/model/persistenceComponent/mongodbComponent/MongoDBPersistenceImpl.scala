package model.persistenceComponent.mongodbComponent


import model.GameInterface
import model.gamefieldComponent.GamefieldInterface
import model.persistenceComponent.mongodbComponent.DAO.GameDAO
import model.persistenceComponent.entity.GameEntity
import model.persistenceComponent.PersistenceInterface
import play.api.libs.json.{JsArray, JsObject, JsString, Json}

import scala.util.{Failure, Success, Try}

import org.mongodb.scala.*

class MongoDBPersistenceImpl() extends PersistenceInterface{

  private val logger = org.slf4j.LoggerFactory.getLogger(classOf[MongoDBPersistenceImpl])

  private val databaseDB: String = sys.env.getOrElse("MONGO_DB", "mongo")
  private val databaseUser: String = sys.env.getOrElse("MONGO_USERNAME", "mongodb")
  private val databasePassword: String = sys.env.getOrElse("MONGO_PASSWORD", "mypassword123")
  private val databasePort: String = sys.env.getOrElse("MONGO_PORT", "27017")
  private val databaseHost: String = sys.env.getOrElse("MONGO_HOST", "localhost")

  // Authentifizierter Verbindungsstring
  private val databaseURI: String =
    s"mongodb://$databaseUser:$databasePassword@$databaseHost:$databasePort/?authSource=admin"

  private val client: MongoClient = MongoClient(databaseURI)


  val db: MongoDatabase = client.getDatabase(databaseDB) // DATABASE: mongo
  private val gameCollection: MongoCollection[Document] = db.getCollection("game")

  private val setup = createTables

  def createTables: Boolean={
    Try{
      db.createCollection("game").head()
    }match
      case Failure(exception) => logger.error(exception.getMessage); false
      case Success(value) => logger.info("Tables created"); true
  }

  @Override
  override def save(game:GameInterface, name:String):Long = {
    GameDAO(db).save(GameEntity(
      0L,
      name,
      game.getGamemech().getWinningBoard(),
      game.getGamemech().getN(),
      gameboardToJason(game.getGamefield().getMap()),
      game.getGamemode().getTargetword(),
      game.getGamemode().getLimit()
    ))
  }

  @Override
  override def load(gameId: Long, game: GameInterface): Unit = {
    println(s"gameId: $gameId")
    val gameEntity = GameDAO(db).findById(gameId)
    gameEntity.winningBoard.size match {
      case 1 =>
        game.changeState(1)
      case 2 =>
        game.changeState(2)
      case 4 =>
        game.changeState(3)
    }

    game.setWinningboard(gameEntity.winningBoard)
    game.setN(gameEntity.versuche)
    game.setMap(gameboardFromJason(gameEntity.gameBoard))
    game.setTargetWord(gameEntity.targetword)
    game.setLimit(gameEntity.limit)
  }

  @Override
  override def search(): String = {
    val gamelogs: Seq[GameEntity] = GameDAO(db).findAll()
    val result: String = gamelogs.map(p => s"${p.gameId} := ${p.name}").mkString("\n")
    println(s"gamelogs:\n $result")
    result
  }

  def dropTable():Unit = db.drop()

  def closeConnection: Unit = client.close()

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
