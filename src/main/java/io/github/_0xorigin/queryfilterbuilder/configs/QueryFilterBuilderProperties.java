package io.github._0xorigin.queryfilterbuilder.configs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

/**
 * Defines the configuration properties for the Query Filter Builder.
 * This record is bound to properties prefixed with "query-filter-builder".
 *
 * @param defaults   Default settings applicable globally.
 * @param queryParam Settings specific to query parameter handling.
 */
@Validated
@ConfigurationProperties(prefix = "query-filter-builder")
public record QueryFilterBuilderProperties(
    @NotNull @NonNull Defaults defaults,
    @NotNull @NonNull QueryParam queryParam
) {
    /**
     * Global default settings.
     *
     * @param fieldDelimiter The character used to separate nested fields in a path (e.g., "." in "customer.name").
     */
    public record Defaults(
        @NotBlank @NonNull String fieldDelimiter
    ) {}

    /**
     * Settings related to query parameter processing.
     *
     * @param defaults Default settings for query parameters.
     */
    public record QueryParam(
        @NotNull @NonNull QueryParamDefaults defaults
    ) {}

    /**
     * Default settings for query parameters.
     *
     * @param sortParameter The name of the query parameter used for sorting (e.g., "sort").
     */
    public record QueryParamDefaults(
        @NotBlank @NonNull String sortParameter
    ) {}
}
