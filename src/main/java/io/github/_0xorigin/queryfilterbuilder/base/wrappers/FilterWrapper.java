package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import io.github._0xorigin.queryfilterbuilder.base.enums.FilterType;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.List;
import java.util.Optional;

/**
 * A wrapper that represents a single, standardized filter request after parsing.
 * This record is used internally to pass filter information between the parser and the builder.
 *
 * @param field             The delimited path to the field (e.g., "customer.address.city").
 * @param originalFieldName The original field name from the request, used for error reporting.
 * @param operator          The filter operator to be applied.
 * @param values            A list of string values to be used by the operator.
 * @param sourceType        The source of the filter (e.g., query parameter or request body).
 * @param filterType        The type of the filter (normal or custom), which is determined in parsing process.
 */
public record FilterWrapper (
    String field,
    String originalFieldName,
    Operator operator,
    List<String> values,
    SourceType sourceType,
    Optional<FilterType> filterType
) {
    /**
     * Creates a new {@code FilterWrapper} instance with the specified {@link FilterType}.
     * This is a wither method that allows for the immutable update of the filter type.
     *
     * @param filterType The filter type to set.
     * @return A new {@code FilterWrapper} instance with the updated filter type.
     */
    public FilterWrapper withFilterType(final FilterType filterType) {
        return new FilterWrapper(field, originalFieldName, operator, values, sourceType, Optional.ofNullable(filterType));
    }
}
