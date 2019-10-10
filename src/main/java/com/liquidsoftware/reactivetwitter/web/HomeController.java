package com.liquidsoftware.reactivetwitter.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidsoftware.reactivetwitter.domain.Tweet;
import com.liquidsoftware.reactivetwitter.domain.TwitterService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private TwitterService twitterService;

    @Autowired
    public HomeController(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    @GetMapping(path = "/sse/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<Tweet> tweets(@RequestParam String topic) {
        LOG.info("GET tweets: {}", topic);
        return twitterService.startFilter(topic)
            .flatMap(twitterService::saveTweet);
    }

    @GetMapping("/sse/kill")
    public void killStream() {
        twitterService.stopFilter();
    }

}
