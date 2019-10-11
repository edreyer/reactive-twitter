package com.liquidsoftware.reactivetwitter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.liquidsoftware")
public class ReactiveTwitterApplication {

    public static void main(String[] args) {
//        System.setProperty("reactor.netty.ioWorkerCount", "500");
//        System.setProperty("reactor.netty.maxConnections", "500");

        SpringApplication.run(ReactiveTwitterApplication.class, args);

    }

}
