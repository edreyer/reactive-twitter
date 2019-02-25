package com.liquidsoftware.reactivetwitter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStreamFactory;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TwitterService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterService.class);

    private Flux<TweetDTO> twitterFlux;
    private StatusListener statusListener;

    private AtomicInteger atomicInt = new AtomicInteger();

    public Flux<TweetDTO> startFilter(String... topics) {
        twitterFlux = Flux.create(sink -> {
            statusListener = new StatusListener() {
                @Override
                public void onStatus(Status status) {
                    sink.next(new TweetDTO(atomicInt.getAndIncrement(), status.getText()));
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
            TwitterStreamFactory
                .getSingleton()
                .addListener(statusListener)
                .filter(topics);
        });
        return twitterFlux
            .doOnNext(t -> LOG.info(t.toString()));
    }

    public void stopFilter() {
        TwitterStreamFactory.getSingleton().removeListener(statusListener);
        TwitterStreamFactory.getSingleton().shutdown();
    }


}
