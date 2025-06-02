package model.persistenceComponent.entity

import model.gamefieldComponent.GamefieldInterface
import play.api.libs.json.{JsArray, JsObject, JsString, JsValue, Json}

case class BoardData(gameId:Long, boardMap:String)
