package run1

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulationwithFisch extends Simulation {

  private val httpProtocol = http
    .baseUrl("http://127.0.0.1:8082")
    .inferHtmlResources()
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.42.0")

  private val headers_0 = Map("Postman-Token" -> "52b4f5ab-7d68-4373-bd47-276e5d172868")

  private val headers_1 = Map("Postman-Token" -> "5d6aaa12-3457-4cfa-b00e-ab50674e2834")

  private val headers_2 = Map("Postman-Token" -> "4a89f700-e300-4615-ae06-0fa277a526da")

  private val headers_3 = Map("Postman-Token" -> "d2ca3a93-cde5-4061-bcc7-5d7fe4c6989e")

  private val headers_4 = Map("Postman-Token" -> "f1df5d6f-91e1-4545-8340-190f7257ec50")

  private val headers_5 = Map("Postman-Token" -> "67820096-e8ce-4211-9213-c89892994455")

  private val headers_6 = Map("Postman-Token" -> "3eab137f-9c67-4c61-9caa-0b0be459a081")

  private val headers_7 = Map("Postman-Token" -> "698a77ab-8df1-4f44-8558-5173d5d7feee")

  private val headers_8 = Map("Postman-Token" -> "f0ba8afc-eef6-4c3c-82e6-74ca40fb0403")

  private val headers_9 = Map("Postman-Token" -> "baf2575e-105e-44f8-9258-211cba0121f5")

  private val headers_10 = Map("Postman-Token" -> "a23b5e78-a8df-4e23-9b2a-1253e0b6aadf")

  private val headers_11 = Map("Postman-Token" -> "855ac9c6-f332-4aff-b4be-041e1d180ea0")

  private val headers_12 = Map("Postman-Token" -> "d7b9b295-d837-4818-b8d1-da7203f78e4c")

  private val headers_13 = Map("Postman-Token" -> "e1eebb84-37cf-44f9-b25c-c8f24767354e")

  private val headers_14 = Map(
  		"Content-Type" -> "application/json",
  		"Postman-Token" -> "6ef3f90b-f84b-4eb7-8b19-814ac85872e1"
  )

  private val headers_15 = Map(
  		"Content-Type" -> "application/json",
  		"Postman-Token" -> "28711c7b-b726-4345-9d03-a3ddc45b54d3"
  )

  private val headers_16 = Map("Postman-Token" -> "3cdf582b-8ecb-4892-b714-6eefda1a18d1")

  private val headers_17 = Map("Postman-Token" -> "8245576e-7bc1-468e-b2dd-b2e6926afc36")

  private val headers_18 = Map("Postman-Token" -> "a0a7ea26-5eae-4998-99d9-ad6a60348320")

  private val headers_19 = Map("Postman-Token" -> "bfce7ee3-7e19-4dc6-a86d-c07ddd32e5c4")

  private val headers_20 = Map("Postman-Token" -> "a715ea86-d50d-4c95-b4c8-8f3529fcd58b")

  private val headers_21 = Map("Postman-Token" -> "a0a79ef5-46ba-4107-82a2-a6db539e46ec")

  private val headers_22 = Map("Postman-Token" -> "2679f3e4-b4c6-4bc1-b245-8442c4ac4a88")

  private val headers_23 = Map("Postman-Token" -> "51ca0349-b556-4d3f-a708-7a6cf92e9c66")

  private val headers_24 = Map(
  		"Content-Type" -> "application/json",
  		"Postman-Token" -> "c373ec06-e7ce-4cdd-9709-10bad7d7feb1"
  )

  private val headers_25 = Map("Postman-Token" -> "cbbe31f3-fb84-43fc-9e90-ed3c6ff3782d")

  private val headers_26 = Map("Postman-Token" -> "5f609010-74a0-488f-87a5-423304bcdfe2")

  private val headers_27 = Map("Postman-Token" -> "b25326a7-8c0a-45ee-8b28-8cc46def822b")

  private val headers_28 = Map("Postman-Token" -> "14ac1127-640b-4013-ad38-e568e9f78733")


  private val scn = scenario("RecordedSimulationwithFisch")
    .exec(
      http("request_0")
        .patch("/model/game/changeState/2")
        .headers(headers_0)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0000_response.txt"))),
      pause(20),
      http("request_1")
        .put("/model/game/createwinningboard")
        .headers(headers_1)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0001_response.txt"))),
      pause(5),
      http("request_2")
        .put("/model/game/createGameboard")
        .headers(headers_2)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0002_response.txt"))),
      pause(10),
      http("request_3")
        .put("/model/game/setN/1")
        .headers(headers_3)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0003_response.txt"))),
      pause(8),
      http("request_4")
        .get("/model/game/toString")
        .headers(headers_4)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0004_response.json"))),
      pause(7),
      http("request_5")
        .put("/model/game/setN/1")
        .headers(headers_5)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0005_response.txt"))),
      pause(5),
      http("request_6")
        .get("/model/game/toString")
        .headers(headers_6)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0006_response.json"))),
      pause(5),
      http("request_7")
        .get("/model/game/GuessTransform/Fisch")
        .headers(headers_7)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0007_response.json"))),
      pause(27),
      http("request_8")
        .get("/model/game/controllLength/5")
        .headers(headers_8)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0008_response.json"))),
      pause(6),
      http("request_9")
        .get("/model/game/controllRealWord/Fisch")
        .headers(headers_9)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0009_response.json"))),
      pause(5),
      http("request_10")
        .get("/model/game/areYouWinningSon/Fisch")
        .headers(headers_10)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0010_response.json"))),
      pause(9),
      http("request_11")
        .get("/model/game/count")
        .headers(headers_11)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0011_response.json"))),
      pause(10),
      http("request_12")
        .get("/model/game/getN")
        .headers(headers_12)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0012_response.json"))),
      pause(7),
      http("request_13")
        .get("/model/game/evaluateGuess/Fisch")
        .headers(headers_13)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0013_response.json"))),
      pause(5),
      http("request_15")
        .post("/model/game/step/1")
        .headers(headers_15)
        .body(RawFileBody("run1/recordedsimulationwithfisch/0015_request.txt"))
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0015_response.txt"))),
      pause(30),
      http("request_16")
        .get("/model/game/getN")
        .headers(headers_16)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0016_response.json"))),
      pause(8),
      http("request_17")
        .get("/model/game/GuessTransform/Fisch")
        .headers(headers_17)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0017_response.json"))),
      pause(11),
      http("request_18")
        .get("/model/game/toString")
        .headers(headers_18)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0018_response.json"))),
      pause(7),
      http("request_19")
        .get("/model/game/controllRealWord/Fisch")
        .headers(headers_19)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0019_response.json"))),
      pause(6),
      http("request_20")
        .get("/model/game/areYouWinningSon/Fisch")
        .headers(headers_20)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0020_response.json"))),
      pause(11),
      http("request_21")
        .get("/model/game/count")
        .headers(headers_21)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0021_response.json"))),
      pause(8),
      http("request_22")
        .get("/model/game/getN")
        .headers(headers_22)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0022_response.json"))),
      pause(6),
      http("request_23")
        .get("/model/game/evaluateGuess/Fisch")
        .headers(headers_23)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0023_response.json"))),
      pause(7),
      http("request_24")
        .post("/model/game/step/1")
        .headers(headers_24)
        .body(RawFileBody("run1/recordedsimulationwithfisch/0024_request.txt"))
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0024_response.txt"))),
      pause(7),
      http("request_25")
        .get("/model/game/getN")
        .headers(headers_25)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0025_response.json"))),
      pause(6),
      http("request_26")
        .get("/model/game/toString")
        .headers(headers_26)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0026_response.json"))),
      pause(1),
      http("request_27")
        .get("/model/game/toString")
        .headers(headers_27)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0027_response.json"))),
      pause(1),
      http("request_28")
        .get("/model/game/toString")
        .headers(headers_28)
        .check(bodyBytes.is(RawFileBody("run1/recordedsimulationwithfisch/0028_response.json")))
    )

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
