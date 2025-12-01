package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Spring configuration for creating the {@link FilterFieldRegistry} bean.
 */
@Configuration
public class FilterFieldRegistryConfig {

    /**
     * Creates the {@link FilterFieldRegistry} bean.
     * This bean collects all available {@link AbstractFilterField} beans from the Spring context
     * and organizes them in a registry for easy lookup by data type.
     *
     * @param filterFields A list of all {@code AbstractFilterField} beans, automatically injected by Spring.
     * @return A new {@link FilterFieldRegistry} instance.
     */
    @Bean
    public FilterFieldRegistry filterFieldRegistry(List<AbstractFilterField<? extends Comparable<?>>> filterFields) {
        return new FilterFieldRegistry(filterFields);
    }
}
