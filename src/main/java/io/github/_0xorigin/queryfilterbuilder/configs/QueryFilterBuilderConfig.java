package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.*;
import io.github._0xorigin.queryfilterbuilder.base.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.Parser;
import io.github._0xorigin.queryfilterbuilder.base.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.QueryFilterBuilder;
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
    public FilterValidator validator() {
        return new FilterValidator();
    }

    @Bean
    public <T> PathGenerator<T> pathGenerator(EntityManager entityManager) {
        return new FilterPathGenerator<T>(entityManager);
    }

    @Bean
    public <T> QueryFilterBuilder<T> queryFilterBuilder(HttpServletRequest request, EntityManager entityManager) {
        return new FilterBuilder<>(parser(request), pathGenerator(entityManager), validator());
    }

    @Bean
    public QueryFilterBuilderExceptionHandler queryFilterBuilderExceptionHandler(MessageSource messageSource) {
        return new QueryFilterBuilderExceptionHandler(messageSource);
    }
}
