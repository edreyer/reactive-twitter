package com.liquidsoftware.reactivetwitter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

import static io.r2dbc.pool.PoolingConnectionFactoryProvider.MAX_SIZE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.PROTOCOL;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.liquidsoftware")
@EnableTransactionManagement
public class RepositoryConfig extends AbstractR2dbcConfiguration {

    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
//        return new PostgresqlConnectionFactory(
//            PostgresqlConnectionConfiguration.builder()
//                .host("localhost")
//                .port(32775)
//                .database("postgres")
//                .username("postgres")
//                .password("")
//                .build()
//        );

        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
            .option(DRIVER, "pool")
            .option(PROTOCOL, "postgresql") // driver identifier, PROTOCOL is delegated as DRIVER by the pool.
            .option(HOST, "localhost")
            .option(PORT, 32775)
            .option(USER, "postgres")
            .option(PASSWORD, "")
            .option(DATABASE, "postgres")
            .option(MAX_SIZE, 30)
            .build());
    }

    @Bean
    ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

//    @Override
//    @Bean
//    public DatabaseClient databaseClient(ReactiveDataAccessStrategy dataAccessStrategy,
//                                         R2dbcExceptionTranslator exceptionTranslator) {
//
//        Assert.notNull(dataAccessStrategy, "DataAccessStrategy must not be null!");
//        Assert.notNull(exceptionTranslator, "ExceptionTranslator must not be null!");
//
//        return DatabaseClient.builder()
//            .connectionFactory(connectionFactory()) //
//            .dataAccessStrategy(dataAccessStrategy) //
//            .exceptionTranslator(exceptionTranslator) //
//            .build();
//    }

}
