package com.liquidsoftware.reactivetwitter.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import static org.springframework.data.r2dbc.query.Criteria.where;

@Component
public class TweetRepositoryImpl implements MyTweetRepository {

    private static final Logger LOG = LoggerFactory.getLogger(TweetRepositoryImpl.class);

    private DatabaseClient client;

    @Autowired
    public TweetRepositoryImpl(DatabaseClient databaseClient) {
        this.client = databaseClient;
    }

    private Mono<Tweet> findTweet(Long id) {
//        return client.execute("SELECT * FROM tweet WHERE id = $1")
//            .bind(0, id)
//            .as(Tweet.class)
//            .fetch()
//            .first();

        return client.select()
            .from(Tweet.class)
            .matching(where("id").is(id))
            .as(Tweet.class)
            .first();

    }
}
