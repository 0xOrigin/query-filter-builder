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
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryFilterBuilderConfig {

    @Bean
    public Parser parser(HttpServletRequest request) {
        return new FilterParser(request);
    }

    @Bean
    public <T> PathGenerator<T> pathGenerator(EntityManager entityManager) {
        return new FilterPathGenerator<>(entityManager);
    }

    @Bean
    public <T> QueryFilterBuilder<T> queryFilterBuilder(
        HttpServletRequest request,
        EntityManager entityManager,
        FilterRegistry filterRegistry,
        FilterOperatorRegistry filterOperatorRegistry
    ) {
        return new FilterBuilder<>(parser(request), pathGenerator(entityManager), filterRegistry, filterOperatorRegistry);
    }

    @Bean
    public QueryFilterBuilderExceptionHandler queryFilterBuilderExceptionHandler(MessageSource messageSource) {
        return new QueryFilterBuilderExceptionHandler(messageSource);
    }
}
