package model.persistenceComponent.mongodbComponent.DAO

import model.persistenceComponent.DAOInterface
import model.persistenceComponent.entity.GameEntity
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase, ObservableFuture, Observer, SingleObservable, documentToUntypedDocument}

import scala.concurrent.Await
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration.*
import org.mongodb.scala.*
import org.mongodb.scala.result.InsertOneResult
import play.api.libs.json.{JsResult, JsValue, Json}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Updates.*
import org.mongodb.scala.result.*


class GameDAO(db: MongoDatabase) extends DAOInterface[GameEntity, Long]{

  val gameCollection: MongoCollection[Document] = db.getCollection("game")

  def loadNextGameId: Long = {
    val results = Await.result(gameCollection.find().toFuture(), 5.seconds)
    val idNew = Try(results.map(_.getLong("gameId")).max + 1L)
    idNew match
      case Success(id) => id
      case Failure(_) => 1L
  }

  @Override
  override def save(obj: GameEntity): Long = {

    def gameToDocument(game:GameEntity, gameId:Long): Document = {
      Document(
        "gameId" -> gameId,
        "name" -> game.name,
        "winningBoard" -> game.winningBoardToJson(),
        "versuche" -> game.versuche,
        "gameboard" -> game.gameBoard,
        "targetWord" -> game.targetWordToJson(),
        "limit" -> game.limit
      )
    }

    val gameId = loadNextGameId

    val document: Document = gameToDocument(obj, gameId)

    val insertObservable: SingleObservable[InsertOneResult] = gameCollection.insertOne(document)

    insertObservable.subscribe(new Observer[InsertOneResult] {
      override def onNext(result: InsertOneResult): Unit = {//wird aufgerufen wenn Einfügen erfolgreich war
        println(s"Inserted: $result")
      }

      override def onError(e: Throwable): Unit = {// wird aufgerufen wenn Einfügen nicht erfolgreich war
        println(s"Failed to insert document: ${e.getMessage}")
      }

      override def onComplete(): Unit = {// wird aufgerufen sobald onNext oder onError abgeschlossen ist
        println("Insertion completed.")
      }
    })

    gameId

  }

  @Override
  override def findAll(): Seq[GameEntity] = {
    val result = Await.result(gameCollection.find().toFuture(), 10.seconds)
    result.map{ doc =>
      GameEntity(
        doc.getLong("gameId"),
        doc.getString("name"),
        winningBoardFromJson(doc.getString("winningBoard")),
        doc.getInteger("versuche"),
        doc.getString("gameboard"),
        targetwordFromJson(doc.getString("targetWord")),
        doc.getInteger("limit")
      )
    }
  }

  @Override
  override def findById(id: Long): GameEntity = {
    val query = Document("gameId" -> id)
    val results = Await.result(gameCollection.find(query).toFuture(), 10.seconds)

    results.headOption match {
      case Some(doc) => GameEntity(
        doc.getLong("gameId"),
        doc.getString("name"),
        winningBoardFromJson(doc.getString("winningBoard")),
        doc.getInteger("versuche"),
        doc.getString("gameboard"),
        targetwordFromJson(doc.getString("targetWord")),
        doc.getInteger("limit")
      )
      case None => GameEntity(0L, "", Map.empty, 0, "", Map.empty, 0)
    }
  }

  @Override
  override def update(id: Long, obj: GameEntity): Unit = {
    val filter: Bson = equal("gameId", id)
    val update: Bson = combine(
      set("name", obj.name),
      set("winningBoard", obj.winningBoardToJson()),
      set("versuche", obj.versuche),
      set("gameboard", obj.gameBoard),
      set("targetWord", obj.targetWordToJson()),
      set("limit", obj.limit)
    )

    gameCollection.updateOne(filter, update).subscribe(
      (updateResult:UpdateResult) => println(s"Updated: ${updateResult.getModifiedCount} document(s)"),
      (t: Throwable) => println(s"Failed: ${t.getMessage}"),
      () => println("Completed update operation")
    )
  }

  @Override
  override def delete(id: Long): Unit = {
    val result = Await.result(gameCollection.deleteOne(equal("gameId", id)).toFuture(), 10.seconds)
  }

  def winningBoardFromJson(winningboard: String): Map[Int, Boolean] = {
    val json: JsValue = Json.parse(winningboard)
    val result: JsResult[Map[Int, Boolean]] = (json \ "winningBoard").validate[Map[Int, Boolean]]
    result.get
  }

  def targetwordFromJson(targetword: String): Map[Int, String] = {
    val json: JsValue = Json.parse(targetword)
    val result: JsResult[Map[Int, String]] = (json \ "TargetWord").validate[Map[Int, String]]
    result.get
  }
}
