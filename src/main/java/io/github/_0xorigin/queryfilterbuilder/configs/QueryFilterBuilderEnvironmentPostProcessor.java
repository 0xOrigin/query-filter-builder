package io.github._0xorigin.queryfilterbuilder.configs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Spring {@link EnvironmentPostProcessor} that sets default configuration properties for the Query Filter Builder.
 * This ensures that the library has sensible defaults for properties like the field delimiter and sort parameter name,
 * even if they are not explicitly configured by the user.
 */
public class QueryFilterBuilderEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /**
     * Adds a {@link MapPropertySource} with default properties to the Spring environment.
     * This method is called by Spring Boot during the application startup process.
     *
     * @param environment The application's configurable environment.
     * @param application The Spring application.
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> defaultProperties = new LinkedHashMap<>();
        defaultProperties.put("query-filter-builder.defaults.field-delimiter", ".");
        defaultProperties.put("query-filter-builder.query-param.defaults.sort-parameter", "sort");

        MapPropertySource propertySource = new MapPropertySource("query-filter-builder", defaultProperties);
        environment.getPropertySources().addLast(propertySource);
    }
}
