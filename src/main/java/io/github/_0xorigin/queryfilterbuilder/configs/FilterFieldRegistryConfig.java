package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FilterFieldRegistryConfig {

    @Bean
    public FilterFieldRegistry filterFieldRegistry(List<AbstractFilterField<? extends Comparable<?>>> filterFields) {
        return new FilterFieldRegistry(filterFields);
    }
}
