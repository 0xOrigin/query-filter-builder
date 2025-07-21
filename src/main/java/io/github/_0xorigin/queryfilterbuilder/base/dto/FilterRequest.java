package io.github._0xorigin.queryfilterbuilder.base.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public record FilterRequest(
    @NotBlank
    String field,
    @NotBlank
    String operator,
    String value
) {}
