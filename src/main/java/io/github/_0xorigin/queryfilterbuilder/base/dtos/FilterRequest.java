package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record FilterRequest(
    @NotBlank
    String field,
    String operator,
    @NotNull
    String value
) {}
