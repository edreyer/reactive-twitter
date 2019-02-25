package com.liquidsoftware.reactivetwitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.liquidsoftware")
public class ReactiveTwitterApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ReactiveTwitterApplication.class);

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(ReactiveTwitterApplication.class, args);

//        TwitterService twitterService = ctx.getBean(TwitterService.class);
//        twitterService
//            .startFilter("trump")
//            .subscribeOn(Schedulers.elastic())
//            .doOnNext(s -> LOG.info(s))
//            .doOnError(e -> LOG.error("ERROR", e))
//            .subscribe();
//        Thread.sleep(10_000);
//        twitterService.stopFilter();
//        System.exit(0);
    }

}
