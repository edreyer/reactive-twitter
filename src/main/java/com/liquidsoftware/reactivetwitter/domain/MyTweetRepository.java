package com.liquidsoftware.reactivetwitter.domain;

import reactor.core.publisher.Flux;

public interface MyTweetRepository {

    Flux<Tweet> saveTweet(Tweet tweet);
}
