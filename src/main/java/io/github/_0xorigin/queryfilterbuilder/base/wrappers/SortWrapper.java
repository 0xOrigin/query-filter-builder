package io.github._0xorigin.queryfilterbuilder.base.wrappers;

import io.github._0xorigin.queryfilterbuilder.base.enums.SortType;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import org.springframework.data.domain.Sort;

import java.util.Optional;

/**
 * A wrapper that represents a single, standardized sort request after parsing.
 * This record is used internally to pass sort information between the parser and the builder.
 *
 * @param field             The delimited path to the field (e.g., "customer.address.city").
 * @param originalFieldName The original field name from the request, used for error reporting.
 * @param direction         The direction of the sort (ASC or DESC).
 * @param sourceType        The source of the sort (e.g., query parameter or request body).
 * @param sortType          The type of the sort (normal or custom), which is determined in parsing process.
 */
public record SortWrapper(
    String field,
    String originalFieldName,
    Sort.Direction direction,
    SourceType sourceType,
    Optional<SortType> sortType
) {
    /**
     * Creates a new {@code SortWrapper} instance with the specified {@link SortType}.
     * This is a wither method that allows for the immutable update of the sort type.
     *
     * @param sortType The sort type to set.
     * @return A new {@code SortWrapper} instance with the updated sort type.
     */
    public SortWrapper withSortType(final SortType sortType) {
        return new SortWrapper(field, originalFieldName, direction, sourceType, Optional.ofNullable(sortType));
    }
}
