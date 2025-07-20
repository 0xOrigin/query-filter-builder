package io.github._0xorigin.queryfilterbuilder.configs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

public class QueryFilterBuilderEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaultProperties = new LinkedHashMap<>();
        defaultProperties.put("query-filter-builder.query-param.defaults.field-delimiter", "__");
        defaultProperties.put("query-filter-builder.request-body", null);

        MapPropertySource propertySource = new MapPropertySource("query-filter-builder", defaultProperties);
        environment.getPropertySources().addLast(propertySource);
    }
}
