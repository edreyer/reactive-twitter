package com.liquidsoftware.reactivetwitter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.jackson.datatype.VavrModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

@Configuration
public class ReactiveTwitterConfig {

    @Value("${twitter.oauth.consumerKey}")
    private String twitterConsumerKey;
    @Value("${twitter.oauth.consumerSecret}")
    private String twitterConsumerSecret;
    @Value("${twitter.oauth.accessToken}")
    private String twitterAccessToken;
    @Value("${twitter.oauth.accessTokenSecret}")
    private String twitterAccessTokenSecret;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return mapper;
    }

    @Bean
    public TwitterStream twitterStream() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
            .setIncludeMyRetweetEnabled(false)
            .setOAuthConsumerKey(twitterConsumerKey)
            .setOAuthConsumerSecret(twitterConsumerSecret)
            .setOAuthAccessToken(twitterAccessToken)
            .setOAuthAccessTokenSecret(twitterAccessTokenSecret);
        return new TwitterStreamFactory(cb.build()).getInstance();
    }

}
