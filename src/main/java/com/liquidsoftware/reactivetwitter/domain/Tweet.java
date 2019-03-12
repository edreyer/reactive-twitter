package com.liquidsoftware.reactivetwitter.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("tweet")
@Data
public class Tweet {

    @Id
    private Long id = null;
    private Long tweetId;
    private String text;
    @Column("retweet")
    private boolean retweet;

    public Tweet(long tweetId, @NonNull String text, boolean retweet) {
        this.tweetId = tweetId;
        this.text = text;
        this.retweet = retweet;
    }

}
