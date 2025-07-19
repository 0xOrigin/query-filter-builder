package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FilterRegistryConfig {

    @Bean
    public FilterRegistry filterRegistry(List<AbstractFilterField<? extends Comparable<?>>> filters) {
        return new FilterRegistry(filters);
    }
}
