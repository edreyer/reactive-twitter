package com.liquidsoftware.reactivetwitter.domain;

import com.google.common.cache.CacheLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.publisher.Mono;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

@Service
public class TwitterService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

    private TwitterStream twitterStream;
    private TweetRepository tweetRepository;

    private Flux<Tweet> twitterFlux;
    private StatusListener statusListener;

    @Autowired
    public TwitterService(
        TwitterStream twitterStream,
        TweetRepository tweetRepository) {
        this.twitterStream = twitterStream;
        this.tweetRepository = tweetRepository;
    }

    @PostConstruct
    public void init() {
        // Setup cache
        CacheLoader<Long, Mono<Tweet>> loader = new CacheLoader<>() {
            @Override
            public Mono<Tweet> load(Long tweetId) {
                return tweetRepository.findByTweetId(tweetId)
                    .doOnNext(t -> LOG.info("LOADED: {}", t))
                    .or(Mono.empty());
            }
        };
    }

    public Flux<Tweet> startFilter(String... topics) {
        LOG.info("Creating Flux");

        Consumer<FluxSink<Tweet>> fluxSink = sink -> {
            statusListener = new StatusAdapter() {
                @Override
                public void onStatus(Status status) {
                    sink.next(new Tweet(status.getId(), status.getText(), status.isRetweet()));
                }
                @Override
                public void onException(Exception ex) {
                    sink.error(ex);
                }
            };
            twitterStream.addListener(statusListener)
                .filter(topics);
        };

        twitterFlux = Flux.create(fluxSink, OverflowStrategy.DROP)
            .filter(tweet -> !tweet.isRetweet());
        return twitterFlux;
    }

    public Mono<Tweet> saveTweet(Tweet tweet) {

        return tweetRepository.findByTweetId(tweet.getTweetId())
            .doOnNext(t -> LOG.info("FOUND {}", t.getTweetId()))
            .switchIfEmpty(
                tweetRepository.save(tweet)
                .doOnNext(t -> LOG.info("SAVED {}", t.getTweetId()))
                .onErrorResume(ex -> tweetRepository
                    .findByTweetId(tweet.getTweetId())
                    .doOnEach(t -> LOG.info("FOUND 2 {}", t)))
            );

    }

    public void stopFilter() {
        twitterStream.removeListener(statusListener);
        twitterStream.shutdown();
    }


}
