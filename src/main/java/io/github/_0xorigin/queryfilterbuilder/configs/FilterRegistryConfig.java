package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;

@Configuration
public class FilterRegistryConfig {

    @Bean
    public <K extends Comparable<? super K> & Serializable> FilterRegistry filterRegistry(List<AbstractFilterField<K>> filters) {
        return new FilterRegistry(filters);
    }
}
