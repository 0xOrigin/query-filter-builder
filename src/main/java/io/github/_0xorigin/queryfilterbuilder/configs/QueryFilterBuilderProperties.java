package io.github._0xorigin.queryfilterbuilder.configs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "query-filter-builder")
public record QueryFilterBuilderProperties(
    @NotNull @NonNull Defaults defaults,
    @NotNull @NonNull QueryParam queryParam
) {
    public record Defaults(
        @NotBlank @NonNull String fieldDelimiter
    ) {}

    public record QueryParam(
        @NotNull @NonNull QueryParamDefaults defaults
    ) {}

    public record QueryParamDefaults(
        @NotBlank @NonNull String sortParameter
    ) {}
}
