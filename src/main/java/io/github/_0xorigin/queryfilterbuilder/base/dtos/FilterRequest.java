package io.github._0xorigin.queryfilterbuilder.base.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * Represents a single filter criterion, typically sent in the body of a request.
 *
 * @param field    The name of the field to filter on. Must not be blank.
 * @param operator The operator to use for the filter (e.g., "eq", "gt"). Can be null, in which case a default is often assumed.
 * @param value    The value to filter by. Must not be null.
 */
@Validated
public record FilterRequest(
    @NotBlank
    String field,
    String operator,
    @NotNull
    String value
) {}
