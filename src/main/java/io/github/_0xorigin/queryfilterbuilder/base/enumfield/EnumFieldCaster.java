package io.github._0xorigin.queryfilterbuilder.base.enumfield;

import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;

/**
 * A functional interface for casting a string value to a specific enum type.
 * Implementations of this interface provide the logic for converting raw string input from a filter request
 * into the actual enum type required for the database query.
 */
@FunctionalInterface
public interface EnumFieldCaster {
    /**
     * Casts the given string value to the target enum type {@code T}.
     *
     * @param enumClass The enum class to cast to.
     * @param value     The string value to cast.
     * @return The casted value of enum type {@code T}.
     * @throws RuntimeException if the string cannot be cast to the target enum type (e.g., {@link IllegalArgumentException}).
     */
    <T extends Enum<T>> T cast(Class<T> enumClass, String value);

    /**
     * Safely casts the given string value, handling any runtime exceptions that occur during casting.
     * If an exception is caught, an error is added to the provided {@link FilterErrorWrapper} and null is returned.
     *
     * @param enumClass          The enum class to cast to.
     * @param value              The string value to cast.
     * @param filterErrorWrapper The wrapper used to collect errors.
     * @return The casted value of enum type {@code T}, or {@code null} if a casting error occurs.
     */
    default <T extends Enum<T>> T safeCast(Class<T> enumClass, String value, FilterErrorWrapper filterErrorWrapper) {
        try {
            return cast(enumClass, value);
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
