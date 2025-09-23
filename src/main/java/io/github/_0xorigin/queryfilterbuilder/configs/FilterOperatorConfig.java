package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.operators.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for creating beans for all supported filter operator implementations.
 * Each bean encapsulates the logic for a specific {@link io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator}.
 */
@Configuration
public class FilterOperatorConfig {

    /**
     * Creates a bean for the 'between' operator.
     * @param localizationService Service for retrieving localized error messages.
     * @return A {@link Between} instance.
     */
    @Bean
    public Between between(LocalizationService localizationService) {
        return new Between(localizationService);
    }

    /**
     * Creates a bean for the 'contains' operator.
     * @return A {@link Contains} instance.
     */
    @Bean
    public Contains contains() {
        return new Contains();
    }

    /**
     * Creates a bean for the 'endsWith' operator.
     * @return An {@link EndsWith} instance.
     */
    @Bean
    public EndsWith endsWith() {
        return new EndsWith();
    }

    /**
     * Creates a bean for the 'equals' operator.
     * @return An {@link Equals} instance.
     */
    @Bean
    public Equals equals() {
        return new Equals();
    }

    /**
     * Creates a bean for the 'greaterThan' operator.
     * @return A {@link GreaterThan} instance.
     */
    @Bean
    public GreaterThan greaterThan() {
        return new GreaterThan();
    }

    /**
     * Creates a bean for the 'greaterThanOrEqual' operator.
     * @return A {@link GreaterThanOrEqual} instance.
     */
    @Bean
    public GreaterThanOrEqual greaterThanOrEqual() {
        return new GreaterThanOrEqual();
    }

    /**
     * Creates a bean for the 'icontains' (case-insensitive contains) operator.
     * @return An {@link IContains} instance.
     */
    @Bean
    public IContains iContains() {
        return new IContains();
    }

    /**
     * Creates a bean for the 'iendsWith' (case-insensitive endsWith) operator.
     * @return An {@link IEndsWith} instance.
     */
    @Bean
    public IEndsWith iEndsWith() {
        return new IEndsWith();
    }

    /**
     * Creates a bean for the 'in' operator.
     * @return An {@link In} instance.
     */
    @Bean
    public In in() {
        return new In();
    }

    /**
     * Creates a bean for the 'isNotNull' operator.
     * @return An {@link IsNotNull} instance.
     */
    @Bean
    public IsNotNull isNotNull() {
        return new IsNotNull();
    }

    /**
     * Creates a bean for the 'isNull' operator.
     * @return An {@link IsNull} instance.
     */
    @Bean
    public IsNull isNull() {
        return new IsNull();
    }

    /**
     * Creates a bean for the 'istartsWith' (case-insensitive startsWith) operator.
     * @return An {@link IStartsWith} instance.
     */
    @Bean
    public IStartsWith iStartsWith() {
        return new IStartsWith();
    }

    /**
     * Creates a bean for the 'lessThan' operator.
     * @return A {@link LessThan} instance.
     */
    @Bean
    public LessThan lessThan() {
        return new LessThan();
    }

    /**
     * Creates a bean for the 'lessThanOrEqual' operator.
     * @return A {@link LessThanOrEqual} instance.
     */
    @Bean
    public LessThanOrEqual lessThanOrEqual() {
        return new LessThanOrEqual();
    }

    /**
     * Creates a bean for the 'notBetween' operator.
     * @param localizationService Service for retrieving localized error messages.
     * @return A {@link NotBetween} instance.
     */
    @Bean
    public NotBetween notBetween(LocalizationService localizationService) {
        return new NotBetween(localizationService);
    }

    /**
     * Creates a bean for the 'notEquals' operator.
     * @return A {@link NotEquals} instance.
     */
    @Bean
    public NotEquals notEquals() {
        return new NotEquals();
    }

    /**
     * Creates a bean for the 'notIn' operator.
     * @return A {@link NotIn} instance.
     */
    @Bean
    public NotIn notIn() {
        return new NotIn();
    }

    /**
     * Creates a bean for the 'startsWith' operator.
     * @return A {@link StartsWith} instance.
     */
    @Bean
    public StartsWith startsWith() {
        return new StartsWith();
    }
}
