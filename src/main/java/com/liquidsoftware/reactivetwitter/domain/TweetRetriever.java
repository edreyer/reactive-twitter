package com.liquidsoftware.reactivetwitter.domain;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import twitter4j.Status;
import twitter4j.StatusAdapter;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

@Service
@AllArgsConstructor
public class TweetRetriever {

    private static final Logger LOG = LoggerFactory.getLogger(TweetRetriever.class);

    private TwitterStream twitterStream;

    // Get's a single tweet for a topic
    public Tweet getTweet(String topic) {
        return getTweetReactive(topic).block();
    }

    // Get's a single tweet for a topic
    public Mono<Tweet> getTweetReactive(String topic) {
        Consumer<MonoSink<Tweet>> monoSink = createMonoSinkConsumer(twitterStream, topic);
        return Mono.create(monoSink);
    }

    // Adapts from the Twitter client API to Reactor's Mono
    private Consumer<MonoSink<Tweet>> createMonoSinkConsumer(TwitterStream twitterStream, String topic) {
        return sink -> {
            StatusListener statusListener = new StatusAdapter() {
                @Override
                public void onStatus(Status status) {
                    // shutdown the stream immediately
                    twitterStream.clearListeners();
                    twitterStream.shutdown();

                    Tweet t = new Tweet(status.getId(), status.getText(), status.isRetweet());
                    LOG.info("Tweet received. Tweet ID: {}", t.getTweetId());
                    sink.success(t);
                }
                @Override
                public void onException(Exception ex) {
                    sink.error(ex);
                }
            };
            twitterStream.addListener(statusListener)
                .filter(topic);
        };
    }

}
