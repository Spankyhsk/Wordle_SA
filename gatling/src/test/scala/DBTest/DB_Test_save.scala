package DBTest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DB_Test_save extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://127.0.0.1:8081/controller")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.42.0")
    .shareConnections
    .maxConnectionsPerHost(1000)


  private val scn = scenario("Test DB Save")
    .exec(
      http("Change Game State")
        .patch("/patchChangeState/3")
        .check(
          status.saveAs("status_ChangeState"),
          bodyString.saveAs("responseBody_ChangeState")
        )
    )
    .exec(
      http("Load Game from Local")
        .get("/getGameSave")
        .check(
          status.saveAs("status_load_local"),
          bodyString.saveAs("responseBody_load_local")
        )
    )
    .exec(
      http("Save Game to DB")
        .put("/putGame/Test")
        .check(
          status.saveAs("status_saveToDB"),
          bodyString.saveAs("responseBody_saveToDB")
        )
    )
    .exec { session =>
      println(s"Change State - Status: ${session("status_ChangeState").asOption[Int].getOrElse(-1)}")
      println(s"Change State - Body: ${session("responseBody_ChangeState").asOption[String].getOrElse("no body")}")

      println(s"Load Local - Status: ${session("status_load_local").asOption[Int].getOrElse(-1)}")
      println(s"Load Local - Body: ${session("responseBody_load_local").asOption[String].getOrElse("no body")}")

      println(s"Save to DB - Status: ${session("status_saveToDB").asOption[Int].getOrElse(-1)}")
      println(s"Save to DB - Body: ${session("responseBody_saveToDB").asOption[String].getOrElse("no body")}")
      session
    }

	//setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(
    scn.inject(
      nothingFor(5.seconds),                        // kleine Wartezeit vor Start
      rampUsersPerSec(1).to(1000).during(20.seconds),
//      constantUsersPerSec(1000) during(30.seconds)
//      atOnceUsers(500),                             // sofortiger Load-Schock
//      rampUsersPerSec(1000).to(10000).during(60.seconds),  // Ramp-up auf 10k/sec
//      constantUsersPerSec(10000).during(60.seconds),       // Haltephase mit 10k/sec
//      rampUsersPerSec(10000).to(20000).during(30.seconds), // weiterer Anstieg
//      constantUsersPerSec(20000).during(30.seconds)        // Endbelastung
    )
  ).protocols(httpProtocol)
}
