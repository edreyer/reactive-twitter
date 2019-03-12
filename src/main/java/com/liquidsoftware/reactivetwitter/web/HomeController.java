package com.liquidsoftware.reactivetwitter.web;

import com.liquidsoftware.reactivetwitter.domain.Tweet;
import com.liquidsoftware.reactivetwitter.domain.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Objects;

@RestController
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private TwitterService twitterService;
    private Flux<Tweet> tweetFlux;

    @Autowired
    public HomeController(
        TwitterService twitterService) {
        this.twitterService = twitterService;
        this.tweetFlux = twitterService
            .startFilter("trump")
            .share();
    }

    @GetMapping(path = "/sse/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<Tweet> tweets() {
        LOG.info("GET tweets");
        return tweetFlux
            .delayElements(Duration.ofSeconds(1))
            .flatMap(twitterService::saveTweet)
            .filter(Objects::nonNull);
    }

    @GetMapping("/kill")
    public void killStream() {
        twitterService.stopFilter();
    }

}
