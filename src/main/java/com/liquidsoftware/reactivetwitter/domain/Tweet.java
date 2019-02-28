package com.liquidsoftware.reactivetwitter.domain;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("tweet")
@Data
@RequiredArgsConstructor
public class Tweet {

    @Id
    private Long id = null;
    @NonNull
    private String text;

}
