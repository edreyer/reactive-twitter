package com.liquidsoftware.reactivetwitter;

import com.liquidsoftware.reactivetwitter.domain.TwitterService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Flux;

@SpringBootApplication(scanBasePackages = "com.liquidsoftware")
public class ReactiveTwitterApplication {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(ReactiveTwitterApplication.class, args);

        TwitterService twitterService = ctx.getBean(TwitterService.class);
        Flux<String> tflux =  twitterService.startFilter("trump");
        tflux.doOnNext(s -> System.out.println(s))
            .subscribe();
        Thread.sleep(10_000);
        twitterService.stopFilter();
        System.exit(0);
    }

}
