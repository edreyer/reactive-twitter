package com.liquidsoftware.reactivetwitter.web;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.liquidsoftware.reactivetwitter.domain.Tweet;
import com.liquidsoftware.reactivetwitter.domain.TwitterService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class TweetController {

    private static final Logger LOG = LoggerFactory.getLogger(TweetController.class);

    private TwitterService twitterService;

    @PostConstruct
    public void init() {
        twitterService.startFilter("trump");
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(path = "/sse/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tweet> tweets(@RequestParam String topic) {
        LOG.info("GET tweets: {}", topic);
        return twitterService.startFilter(topic)
            .flatMap(twitterService::saveTweet);
    }

    @GetMapping(path = "/get-tweet-reactive")
    public Mono<Tweet> getTweetReactive() {
        return twitterService.getTweetReactive();
    }

    @GetMapping(path = "/get-tweet")
    public Tweet getTweet() {
        return twitterService.getTweet();
    }

    @GetMapping("/sse/kill")
    public void killStream() {
        twitterService.stopFilter();
    }

}
