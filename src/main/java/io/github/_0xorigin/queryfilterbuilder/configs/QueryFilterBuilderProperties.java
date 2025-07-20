package io.github._0xorigin.queryfilterbuilder.configs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "query-filter-builder")
public record QueryFilterBuilderProperties(
    @NotNull
    @NonNull
    QueryParam queryParam,

    RequestBody requestBody
) {
    public record QueryParam(
        QueryParamDefaults defaults
    ) {}

    public record RequestBody() {}

    public record QueryParamDefaults(
        @NotBlank
        @NonNull
        String fieldDelimiter
    ) {}
}
