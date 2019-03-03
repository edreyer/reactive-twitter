package com.liquidsoftware.reactivetwitter.domain;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

import javax.annotation.PostConstruct;

@Service
public class TwitterService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

    private TwitterStream twitterStream;
    private TweetRepository tweetRepository;

    private Flux<Tweet> twitterFlux;
    private StatusListener statusListener;
    LoadingCache<String, Mono<Tweet>> tweetLRUCache;

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
        CacheLoader<String, Mono<Tweet>> loader = new CacheLoader<>() {
            @Override
            public Mono<Tweet> load(String hash) {
                LOG.info("Loading from DB.  hash={}", hash);
                return tweetRepository.findByHash(hash);
            }
        };
        tweetLRUCache = CacheBuilder.newBuilder().maximumSize(500).build(loader);
    }

    public Flux<Tweet> startFilter(String... topics) {
        LOG.info("Creating Flux");
        twitterFlux = Flux.create(sink -> {
            statusListener = new StatusListener() {
                @Override
                public void onStatus(Status status) {
                    sink.next(new Tweet(status.getText()));
                }
                @Override
                public void onException(Exception ex) {
                    sink.error(ex);
                }
                @Override
                public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }
                @Override
                public void onTrackLimitationNotice(int numberOfLimitedStatuses) { }
                @Override
                public void onScrubGeo(long userId, long upToStatusId) { }
                @Override
                public void onStallWarning(StallWarning warning) { }
            };
            twitterStream.addListener(statusListener)
                .filter(topics);
        });
        return twitterFlux;
    }

    public Mono<Tweet> saveTweet(Tweet tweet) {
        return tweetRepository
            .save(tweet)
            .onErrorResume((ex) -> {
                switch (ex.getClass().getSimpleName()) {
                    case "DuplicateKeyException":
                        return tweetLRUCache.getUnchecked(tweet.getHash());
                    default:
                        LOG.error("Failed to save tweet: {}", ex, tweet);
                }
                return Mono.empty();
            });
    }

    public void stopFilter() {
        twitterStream.removeListener(statusListener);
        twitterStream.shutdown();
    }


}
