package com.liquidsoftware.reactivetwitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.liquidsoftware")
public class ReactiveTwitterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveTwitterApplication.class, args);

    }

}
