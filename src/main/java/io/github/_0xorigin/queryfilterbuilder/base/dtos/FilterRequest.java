package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record FilterRequest(
    @NotBlank
    String field,
    String operator,
    @NotBlank
    String value
) {}
