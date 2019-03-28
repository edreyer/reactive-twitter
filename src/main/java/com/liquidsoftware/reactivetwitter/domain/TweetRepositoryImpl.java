package com.liquidsoftware.reactivetwitter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.data.r2dbc.function.TransactionalDatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TweetRepositoryImpl implements MyTweetRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TweetRepositoryImpl.class);

    private TransactionalDatabaseClient client;

    @Autowired
    public TweetRepositoryImpl(TransactionalDatabaseClient databaseClient) {
        this.client = databaseClient;
    }

    @Override
    public Flux<Tweet> saveTweet(Tweet tweet) {
        return client.inTransaction(db ->
            findTweet(tweet, db)
                .switchIfEmpty(
                    writeTweet(tweet, db)
                        .then(findTweet(tweet, db))
                )
        );
    }

    private Mono<Integer> writeTweet(Tweet tweet, DatabaseClient db) {
        LOG.info("Inserting Tweet {}", tweet.getTweetId());
        return db.execute()
            .sql("INSERT INTO tweet (tweet_id, retweet, text) values ($1, $2, $3)")
            .bind(0, tweet.getTweetId())
            .bind(1, tweet.isRetweet())
            .bind(2, tweet.getText())
            .fetch()
            .rowsUpdated();
    }

    private Mono<Tweet> findTweet(Tweet tweet, DatabaseClient db) {
        return db.execute()
            .sql("SELECT * FROM tweet WHERE tweet_id = $1")
            .bind(0, tweet.getTweetId())
            .as(Tweet.class)
            .fetch()
            .first();
    }
}
