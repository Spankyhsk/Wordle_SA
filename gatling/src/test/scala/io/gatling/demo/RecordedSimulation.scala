//package io.gatling.demo
//
//import scala.concurrent.duration._
//
//import io.gatling.core.Predef._
//import io.gatling.http.Predef._
//import io.gatling.jdbc.Predef._
//
//class RecordedSimulation extends Simulation {
//
//  private val httpProtocol = http
//    .baseUrl("http://127.0.0.1:8082")
//    .inferHtmlResources()
//    .acceptHeader("image/avif,image/webp,image/png,image/svg+xml,image/*;q=0.8,*/*;q=0.5")
//    .acceptEncodingHeader("gzip, deflate, br")
//    .acceptLanguageHeader("de,en-US;q=0.7,en;q=0.3")
//    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:138.0) Gecko/20100101 Firefox/138.0")
//
//  private val headers_0 = Map(
//  		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
//  		"Priority" -> "u=0, i",
//  		"Sec-Fetch-Dest" -> "document",
//  		"Sec-Fetch-Mode" -> "navigate",
//  		"Sec-Fetch-Site" -> "none",
//  		"Sec-Fetch-User" -> "?1",
//  		"Upgrade-Insecure-Requests" -> "1"
//  )
//
//  private val headers_1 = Map(
//  		"Priority" -> "u=6",
//  		"Sec-Fetch-Dest" -> "image",
//  		"Sec-Fetch-Mode" -> "no-cors",
//  		"Sec-Fetch-Site" -> "cross-site"
//  )
//
//
//  private val scn = scenario("RecordedSimulation")
//    .exec(
//      http("request_0")
//        .get("/model/game/count")
//        .headers(headers_0)
//        .resources(
//          http("request_1")
//            .get("/favicon.ico")
//            .headers(headers_1)
//            .check(status.is(404))
//        )
//    )
//
//	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
//}
