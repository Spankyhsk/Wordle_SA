package util.kafka.EventType

import spray.json._
import spray.json.DefaultJsonProtocol._

object EventJsonProtocol {
  import EventType.*

  // JSON Format for EventType
  implicit object EventTypeFormat extends RootJsonFormat[EventType] {
    def write(e: EventType): JsValue = JsString(e.toString)
    def read(value: JsValue): EventType = value match {
      case JsString(str) => EventType.valueOf(str)
      case _ => throw new DeserializationException("Expected EventType as JsString")
    }
  }

  // JSON Format für Event
  implicit val eventFormat: RootJsonFormat[Event] = jsonFormat2(Event.apply)
}
