package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring configuration for creating the {@link FilterOperatorRegistry} bean.
 */
@Configuration
public class FilterOperatorRegistryConfig {

    /**
     * Creates the {@link FilterOperatorRegistry} bean.
     * This bean collects all available {@link FilterOperator} beans from the Spring context
     * and organizes them in a registry for easy lookup by {@link io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator} constant.
     *
     * @param operators A list of all {@code FilterOperator} beans, automatically injected by Spring.
     * @return A new {@link FilterOperatorRegistry} instance.
     */
    @Bean
    public FilterOperatorRegistry filterOperatorRegistry(List<FilterOperator> operators) {
        return new FilterOperatorRegistry(operators);
    }
}
