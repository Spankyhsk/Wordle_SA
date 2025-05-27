package DBTest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class DB_Test_load extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://127.0.0.1:8081/controller")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.42.0")
    .shareConnections
    .maxConnectionsPerHost(1000)

  private val setupScenario = scenario("Setup for Load Test")
    .exec(
      http("Change Game State")
        .patch("/patchChangeState/3")
        .check(status.is(200))
    )
    .exec(
      http("Load Game from Local")
        .get("/getGameSave")
        .check(status.is(200))
    )
    .exec(
      http("Save Game to DB")
        .put("/putGame/LoadTest")
        .check(status.is(200))
    )


  private val scn = scenario("Test DB Load")
    .exec(
      http("Change Game State")
        .patch("/patchChangeState/3")
        .check(status.is(200))
    )
    .exec(
      http("Load Game from DB")
        .put("/getGame/1")
//        .check(
//          status.saveAs("status_LoadToDB"),
//          bodyString.saveAs("responseBody_LoadToDB")
//        )
    )
//    .exec { session =>
//      println(s"Load to DB - Status: ${session("status_LoadToDB").asOption[Int].getOrElse(-1)}")
//      println(s"Load to DB - Body: ${session("responseBody_LoadToDB").asOption[String].getOrElse("no body")}")
//      session
//    }

	//setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(
    setupScenario.inject(atOnceUsers(1)) // Setup-Szenario einmal ausführen
      .andThen(
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
      )
  ).protocols(httpProtocol)
}
