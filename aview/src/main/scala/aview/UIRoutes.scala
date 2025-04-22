package aview

import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ContentTypes

class UIRoutes(TUI: TUI, GUISWING: GUISWING) {

  val routes: Route ={
    get {
      path("tui"){
        complete("tui")
      }
    }
  }
}
