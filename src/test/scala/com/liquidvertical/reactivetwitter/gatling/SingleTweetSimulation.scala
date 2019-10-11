package com.liquidvertical.reactivetwitter.gatling

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.sse.SseMessageCheck

import scala.concurrent.duration._

class SingleTweetSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:8080") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val maxUsers = 150
  val duration = 20
  val maxMessages = 1

  // check
//  val tweetChecks: Array[SseMessageCheck] = (0 until maxMessages).map(_ => {
//    sse.checkMessage("check tweet")
//      .check(jsonPath("""$..data""").saveAs("tweet"))
//  }).toArray

  val tweetClient = scenario("Tweet Client")
    .exec(
      http("Get Tweets")
        .get("/get-tweet")
        .check(jsonPath("""$..tweetId""").saveAs("tweet"))
    )
//    .exec(sse("Close").close())
//    .exec(
//      http("Kill Tweets")
//        .get("/sse/kill")
//        .check(status.is(200))
//    )

  setUp(
    tweetClient
      .inject(constantConcurrentUsers(maxUsers) during (duration seconds))
      .protocols(httpProtocol)
  )

  before {
    println("Simulation is about to start!")
  }

  after {
    println("Simulation is finished!")
  }

}
