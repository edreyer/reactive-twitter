package com.liquidsoftware.reactivetwitter.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquidsoftware.reactivetwitter.domain.TweetDTO;
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

@RestController
public class HomeController {

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private ObjectMapper objectMapper;
    private TwitterService twitterService;
    private Flux<TweetDTO> tweetFlux;

    @Autowired
    public HomeController(ObjectMapper objectMapper, TwitterService twitterService) {
        this.objectMapper = objectMapper;
        this.twitterService = twitterService;
        this.tweetFlux = twitterService
            .startFilter("trump")
            .share();
    }

    @GetMapping(path = "/sse/tweets", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin(origins = "http://localhost:3000")
    public Flux<String> tweets() {
        LOG.info("GET tweets");
        return tweetFlux
            .delayElements(Duration.ofSeconds(1))
            .map(tweet -> {
                try {
                    return objectMapper.writeValueAsString(tweet) + "\n\n";
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    @GetMapping("/kill")
    public void killStream() {
        twitterService.stopFilter();
    }

}
