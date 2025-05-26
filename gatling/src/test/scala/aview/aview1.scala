package run1

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class aview1 extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://127.0.0.1:8080/ui/tui/")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.42.0")


  private val scn = scenario("RecordedSimulationwithFisch")
    .exec(
      http("request_0")
        .patch("/model/game/changeState/2"),
      http("request_1")
        .put("/model/game/createwinningboard"),
      http("request_2")
        .put("/model/game/createGameboard"),
      http("request_3")
        .put("/model/game/setN/1"),
      http("request_4")
        .get("/model/game/toString"),
      http("request_5")
        .put("/model/game/setN/1"),
      http("request_6")
        .get("/model/game/toString"),
      http("request_7")
        .get("/model/game/GuessTransform/Fisch"),
      http("request_8")
        .get("/model/game/controllLength/5"),
      http("request_9")
        .get("/model/game/controllRealWord/Fisch"),
      http("request_10")
        .get("/model/game/areYouWinningSon/Fisch"),
      http("request_11")
        .get("/model/game/count"),
      http("request_12")
        .get("/model/game/getN"),
      http("request_13")
        .get("/model/game/evaluateGuess/Fisch"),
      http("request_15")
        .post("/model/game/step/1")
        .body(RawFileBody("run1/recordedsimulationwithfisch/0015_request.txt")),
      http("request_16")
        .get("/model/game/getN"),
      http("request_17")
        .get("/model/game/GuessTransform/Fisch"),
      http("request_18")
        .get("/model/game/toString"),
      http("request_19")
        .get("/model/game/controllRealWord/Fisch"),
      http("request_20")
        .get("/model/game/areYouWinningSon/Fisch"),
      http("request_21")
        .get("/model/game/count"),
      http("request_22")
        .get("/model/game/getN"),
      http("request_23")
        .get("/model/game/evaluateGuess/Fisch"),
      http("request_24")
        .post("/model/game/step/1")
        .body(RawFileBody("run1/recordedsimulationwithfisch/0024_request.txt")),
      http("request_25")
        .get("/model/game/getN"),
      http("request_26")
        .get("/model/game/toString"),
      http("request_27")
        .get("/model/game/toString"),
      http("request_28")
        .get("/model/game/toString")
    )

	//setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
  setUp(
    scn.inject(
      constantUsersPerSec(1000) during (30.seconds),
      rampUsersPerSec(1000).to(10000).during(60.seconds),
      constantUsersPerSec(10000) during (60.seconds),
      rampUsersPerSec(10000).to(50000).during(60.seconds)
    )
  ).protocols(httpProtocol)
}
