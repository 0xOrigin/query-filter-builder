package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

@Validated
public record SortRequest(
    @NotBlank
    String field,
    @NotBlank
    Sort.Direction direction
) {}
