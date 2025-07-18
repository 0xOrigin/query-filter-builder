package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FilterOperatorRegistryConfig {

    @Bean
    public FilterOperatorRegistry filterOperatorRegistry(List<FilterOperator> operators) {
        return new FilterOperatorRegistry(operators);
    }
}
