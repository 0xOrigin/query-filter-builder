package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;

import java.io.Serializable;

/**
 * A functional interface for casting a string value to a specific data type.
 * Implementations of this interface provide the logic for converting raw string input from a filter request
 * into the actual type required for the database query.
 *
 * @param <T> The target type to which the string will be cast.
 */
@FunctionalInterface
public interface FieldCaster<T extends Comparable<? super T> & Serializable> {

    /**
     * Casts the given string value to the target type {@code T}.
     *
     * @param value The string value to cast.
     * @return The casted value of type {@code T}.
     * @throws RuntimeException if the string cannot be cast to the target type (e.g., {@link NumberFormatException}).
     */
    T cast(String value);

    /**
     * Safely casts the given string value, handling any runtime exceptions that occur during casting.
     * If an exception is caught, an error is added to the provided {@link FilterErrorWrapper} and null is returned.
     *
     * @param value              The string value to cast.
     * @param filterErrorWrapper The wrapper used to collect errors.
     * @return The casted value of type {@code T}, or {@code null} if a casting error occurs.
     */
    default T safeCast(String value, FilterErrorWrapper filterErrorWrapper) {
        try {
            return cast(value);
        } catch (RuntimeException exception) {
            FilterUtils.addFieldError(
                filterErrorWrapper.bindingResult(),
                filterErrorWrapper.filterWrapper().originalFieldName(),
                value,
                exception.getLocalizedMessage()
            );
            return null;
        }
    }
}
