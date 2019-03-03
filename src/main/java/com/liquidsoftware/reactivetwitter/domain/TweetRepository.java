package com.liquidsoftware.reactivetwitter.domain;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TweetRepository extends R2dbcRepository<Tweet, Long> {

    @Query("select * from tweet where hash = $1 ")
    Mono<Tweet> findByHash(String hash);

}
