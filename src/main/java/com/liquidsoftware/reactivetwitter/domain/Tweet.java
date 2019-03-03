package com.liquidsoftware.reactivetwitter.domain;

import com.liquidsoftware.reactivetwitter.utils.CryptoUtils;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("tweet")
@Data
public class Tweet {

    @Id
    private Long id = null;
    @NonNull
    private String hash;
    @NonNull
    private String text;

    public Tweet(@NonNull String text) {
        this.text = text;
        this.hash = CryptoUtils.encode(text);
    }

}
