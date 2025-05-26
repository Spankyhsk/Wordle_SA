package io.gatling.demo

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class rec2_unmodified extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://127.0.0.1:8080")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.44.0")

  private val headers_0 = Map("Postman-Token" -> "99243560-5331-4c10-a0a6-2fcbdafc2bce")

  private val headers_1 = Map("Postman-Token" -> "55e13dc2-e3f9-49fd-8511-9efc0a77379a")

  private val headers_2 = Map("Postman-Token" -> "c97f4ea4-57dd-411f-95a0-6ea5b3a964a1")


  private val scn = scenario("RecordedSimulationwithFisch")
    .exec(
      http("request_0")
        .get("/ui/tui/getNewGame")
        .headers(headers_0)
        .check(bodyBytes.is(RawFileBody("io/gatling/demo/recordedsimulationwithfisch/0000_response.json"))),
      pause(5),
      http("request_1")
        .get("/ui/tui/Select")
        .headers(headers_1)
        .check(bodyBytes.is(RawFileBody("io/gatling/demo/recordedsimulationwithfisch/0001_response.txt"))),
      pause(3),
      http("request_2")
        .put("/ui/tui/processInput/1")
        .headers(headers_2)
        .check(bodyBytes.is(RawFileBody("io/gatling/demo/recordedsimulationwithfisch/0002_response.txt")))
    )

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
