package run2

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class rec2_no_pauses extends Simulation {


  private val httpProtocol = http
    .baseUrl("http://127.0.0.1:8080")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.44.0")

  private val scn = scenario("RecordedSimulationwithFisch")
    .exec(
      http("request_0")
        .get("/ui/tui/getNewGame"),
      http("request_1")
        .get("/ui/tui/Select"),
      http("request_2")
        .put("/ui/tui/processInput/1")
        .body(RawFileBody("run2/0002_response.txt"))
    )


	//setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(
    scn.inject(
      constantUsersPerSec(100) during (30.seconds),
      rampUsersPerSec(100).to(500).during(60.seconds),
      constantUsersPerSec(100) during (60.seconds),
      rampUsersPerSec(100).to(500).during(60.seconds)
    )
  ).protocols(httpProtocol)
}
