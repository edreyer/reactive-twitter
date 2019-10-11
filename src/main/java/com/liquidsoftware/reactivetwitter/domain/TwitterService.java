package com.liquidsoftware.reactivetwitter.domain;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.vavr.control.Try;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

@Service
public class TwitterService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

    private TwitterStream twitterStream;
    private TweetRepository tweetRepository;

    private volatile Flux<Tweet> twitterFlux;
    private StatusListener statusListener;

    @Autowired
    public TwitterService(
        TwitterStream twitterStream,
        TweetRepository tweetRepository) {
        this.twitterStream = twitterStream;
        this.tweetRepository = tweetRepository;
    }

    @PostConstruct
    public void init() { }

    public Flux<Tweet> startFilter(String topic) {
        if (twitterFlux == null) {
            synchronized (this) {
                if (twitterFlux == null) {
                    LOG.info("Creating Flux");

                    Consumer<FluxSink<Tweet>> fluxSink = sink -> {
                        statusListener = new StatusAdapter() {
                            @Override
                            public void onStatus(Status status) {
                                Tweet t = new Tweet(status.getId(), status.getText(), status.isRetweet());
                                //LOG.info("onStatus: {}", t.getTweetId());
                                sink.next(t);
                            }
                            @Override
                            public void onException(Exception ex) {
                                sink.error(ex);
                            }
                        };

                        twitterStream.clearListeners().addListener(statusListener)
                            .filter(topic);

                        sink.onDispose(() -> twitterStream.clearListeners());
                    };

                    twitterFlux = Flux.create(fluxSink, OverflowStrategy.DROP)
                        .filter(tweet -> !tweet.isRetweet())
                        .share();
                }
            }
        }

        // Add a subscriber to keep the flux open
        twitterFlux.
            subscribeOn(Schedulers.elastic()).
            subscribe((tweet) -> Try.run(() -> Thread.sleep(1)));

        return twitterFlux;
    }

    public Mono<Tweet> getTweetReactive() {
        return twitterFlux
            .next();
    }

    public Tweet getTweet() {
        return getTweetReactive()
            .block();
    }

    public Mono<Tweet> findByTwitterId(Long twitterId) {
        return tweetRepository.findByTweetId(twitterId);
    }

    @Transactional
    public Mono<Tweet> saveTweet(Tweet tweet) {
        return findByTwitterId(tweet.getTweetId())
            .switchIfEmpty(tweetRepository.save(tweet))
            .onErrorResume(e -> findByTwitterId(tweet.getTweetId()));
    }


    public void stopFilter() {
        LOG.info("Killing Twitter Stream");
        twitterStream.removeListener(statusListener);
        twitterStream.shutdown();
        twitterFlux = null;

    }


}
