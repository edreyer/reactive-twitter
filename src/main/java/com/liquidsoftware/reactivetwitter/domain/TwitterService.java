package com.liquidsoftware.reactivetwitter.domain;

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
                        return tweetRepository.findByHash(tweet.getHash());
                    default:
                        LOG.error("Failed to save tweet: {}", ex, tweet);
                }
                return null;
            });
    }

    public void stopFilter() {
        twitterStream.removeListener(statusListener);
        twitterStream.shutdown();
    }


}
