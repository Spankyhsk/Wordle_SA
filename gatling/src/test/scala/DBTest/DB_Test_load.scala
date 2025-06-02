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
        .check(status.is(200))
    )
//    .exec { session =>
//      println(s"Load to DB - Status: ${session("status_LoadToDB").asOption[Int].getOrElse(-1)}")
//      println(s"Load to DB - Body: ${session("responseBody_LoadToDB").asOption[String].getOrElse("no body")}")
//      session
//    }

  // ========== Testarten ==========

  // 1. Load Test: moderate Steigerung bis normale Last
  val loadTest = scn.inject(
    nothingFor(5.seconds),
    rampUsersPerSec(1).to(1000).during(60.seconds)
  )

  // 2. Stress Test: was passiert bei Überlast?
  val stressTest = scn.inject(
    rampUsersPerSec(1000).to(10000).during(30.seconds),
    constantUsersPerSec(10000).during(30.seconds)
  )

  // 3. Volume Test: viele Daten (würde man mit verschiedenen Datensätzen erweitern)
  val volumeTest = scn.inject(
    constantUsersPerSec(500).during(30.seconds) // Simuliere viele Anfragen mit großen Datenmengen
  )

  // 4. Endurance Test: wie lange hält das System durch?
  val enduranceTest = scn.inject(
    constantUsersPerSec(100).during(1.hour)
  )

  // 5. Spike Test: plötzliche Lastspitze
  val spikeTest = scn.inject(
    nothingFor(5.seconds),
    atOnceUsers(5000),
    nothingFor(10.seconds),
    atOnceUsers(10000)
  )

  // ========== SetUp ==========

  setUp(
    setupScenario.inject(atOnceUsers(1)) // Setup einmal ausführen
      .andThen(
        // Hier den Test wähen der durchgeführt werden soll:

        loadTest
        // stressTest
        // volumeTest
        // enduranceTest
        // spikeTest
      )
  ).protocols(httpProtocol)
}
