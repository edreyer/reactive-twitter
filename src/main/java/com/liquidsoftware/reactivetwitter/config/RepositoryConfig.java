package com.liquidsoftware.reactivetwitter.config;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.function.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.function.TransactionalDatabaseClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.r2dbc.support.R2dbcExceptionTranslator;
import org.springframework.util.Assert;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.liquidsoftware")
public class RepositoryConfig extends AbstractR2dbcConfiguration {

    @Override
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host("localhost")
                .port(5432)
                .database("postgres")
                .username("postgres")
                .password("")
                .build()
        );
    }

    @Override
    @Bean
    public TransactionalDatabaseClient databaseClient(ReactiveDataAccessStrategy dataAccessStrategy,
                                         R2dbcExceptionTranslator exceptionTranslator) {

        Assert.notNull(dataAccessStrategy, "DataAccessStrategy must not be null!");
        Assert.notNull(exceptionTranslator, "ExceptionTranslator must not be null!");

        return TransactionalDatabaseClient.builder() //
            .connectionFactory(connectionFactory()) //
            .dataAccessStrategy(dataAccessStrategy) //
            .exceptionTranslator(exceptionTranslator) //
            .build();
    }

}
