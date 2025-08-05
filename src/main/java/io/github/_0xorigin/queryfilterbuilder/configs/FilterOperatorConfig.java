package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.operators.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterOperatorConfig {

    @Bean
    public Between between(LocalizationService localizationService) {
        return new Between(localizationService);
    }

    @Bean
    public Contains contains() {
        return new Contains();
    }

    @Bean
    public EndsWith endsWith() {
        return new EndsWith();
    }

    @Bean
    public Equals equals() {
        return new Equals();
    }

    @Bean
    public GreaterThan greaterThan() {
        return new GreaterThan();
    }

    @Bean
    public GreaterThanOrEqual greaterThanOrEqual() {
        return new GreaterThanOrEqual();
    }

    @Bean
    public IContains iContains() {
        return new IContains();
    }

    @Bean
    public IEndsWith iEndsWith() {
        return new IEndsWith();
    }

    @Bean
    public In in() {
        return new In();
    }

    @Bean
    public IsNotNull isNotNull() {
        return new IsNotNull();
    }

    @Bean
    public IsNull isNull() {
        return new IsNull();
    }

    @Bean
    public IStartsWith iStartsWith() {
        return new IStartsWith();
    }

    @Bean
    public LessThan lessThan() {
        return new LessThan();
    }

    @Bean
    public LessThanOrEqual lessThanOrEqual() {
        return new LessThanOrEqual();
    }

    @Bean
    public NotBetween notBetween(LocalizationService localizationService) {
        return new NotBetween(localizationService);
    }

    @Bean
    public NotEquals notEquals() {
        return new NotEquals();
    }

    @Bean
    public NotIn notIn() {
        return new NotIn();
    }

    @Bean
    public StartsWith startsWith() {
        return new StartsWith();
    }
}
