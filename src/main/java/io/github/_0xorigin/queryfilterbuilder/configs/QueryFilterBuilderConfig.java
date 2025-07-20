package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.FilterBuilder;
import io.github._0xorigin.queryfilterbuilder.FilterParser;
import io.github._0xorigin.queryfilterbuilder.FilterPathGenerator;
import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilderExceptionHandler;
import io.github._0xorigin.queryfilterbuilder.base.Parser;
import io.github._0xorigin.queryfilterbuilder.base.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.QueryFilterBuilder;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QueryFilterBuilderProperties.class)
public class QueryFilterBuilderConfig {

    @Bean
    public Parser parser(QueryFilterBuilderProperties properties) {
        return new FilterParser(properties);
    }

    @Bean
    public Metamodel metamodel(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.getMetamodel();
    }

    @Bean
    public <T> PathGenerator<T> pathGenerator(EntityManagerFactory entityManagerFactory, QueryFilterBuilderProperties properties) {
        return new FilterPathGenerator<>(metamodel(entityManagerFactory), properties);
    }

    @Bean
    public <T> QueryFilterBuilder<T> queryFilterBuilder(
        EntityManagerFactory entityManagerFactory,
        FilterRegistry filterRegistry,
        FilterOperatorRegistry filterOperatorRegistry,
        QueryFilterBuilderProperties properties
    ) {
        return new FilterBuilder<>(parser(properties), pathGenerator(entityManagerFactory, properties), filterRegistry, filterOperatorRegistry);
    }

    @Bean
    public QueryFilterBuilderExceptionHandler queryFilterBuilderExceptionHandler(MessageSource messageSource) {
        return new QueryFilterBuilderExceptionHandler(messageSource);
    }
}
