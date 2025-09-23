package io.github._0xorigin.queryfilterbuilder.base.utils;

import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryParameterException;
import io.github._0xorigin.queryfilterbuilder.exceptions.QueryBuilderConfigurationException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A utility class providing helper methods for the filter-building process.
 * This class cannot be instantiated.
 */
public final class FilterUtils {

    private FilterUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a {@link FieldError} with detailed information.
     *
     * @param bindingResult  The binding result to associate the error with.
     * @param fieldName      The name of the field that has the error.
     * @param value          The rejected value.
     * @param bindingFailure Whether this error represents a data binding failure.
     * @param codes          The error codes to resolve to messages.
     * @param arguments      The arguments to be used when resolving the message.
     * @param defaultMessage The default message to use if no specific message is found.
     * @return A new {@link FieldError} instance.
     */
    public static FieldError generateFieldError(
        BindingResult bindingResult,
        String fieldName,
        String value,
        boolean bindingFailure,
        @Nullable String[] codes,
        @Nullable Object[] arguments,
        @Nullable String defaultMessage
    ) {
        return new FieldError(bindingResult.getObjectName(), fieldName, value, bindingFailure, codes, arguments, defaultMessage);
    }

    /**
     * Generates a simple {@link FieldError} with a default message.
     *
     * @param bindingResult  The binding result to associate the error with.
     * @param fieldName      The name of the field that has the error.
     * @param value          The rejected value.
     * @param defaultMessage The default error message.
     * @return A new {@link FieldError} instance.
     */
    public static FieldError generateFieldError(BindingResult bindingResult, String fieldName, String value, String defaultMessage) {
        return generateFieldError(bindingResult, fieldName, value, false, null, null, defaultMessage);
    }

    /**
     * Adds a simple field error to the given {@link BindingResult}.
     *
     * @param bindingResult  The binding result to which the error will be added.
     * @param fieldName      The name of the field that has the error.
     * @param value          The rejected value.
     * @param defaultMessage The default error message.
     */
    public static void addFieldError(BindingResult bindingResult, String fieldName, String value, String defaultMessage) {
        bindingResult.addError(generateFieldError(bindingResult, fieldName, value, defaultMessage));
    }

    /**
     * Adds a pre-constructed {@link ObjectError} to the given {@link BindingResult}.
     *
     * @param bindingResult The binding result to which the error will be added.
     * @param objectError   The error to add.
     */
    public static void addError(BindingResult bindingResult, ObjectError objectError) {
        bindingResult.addError(objectError);
    }

    /**
     * Splits a string by a delimiter, respecting URL-encoded delimiters (e.g., %2C for comma).
     * Decodes each part using URL decoding.
     *
     * @param value     The string to split.
     * @param delimiter The raw delimiter to split by (not URL-encoded).
     * @return An array of strings.
     */
    public static String[] splitWithEscapedDelimiter(String value, String delimiter) {
        if (value == null || value.isEmpty() || delimiter == null || delimiter.isEmpty()) {
            return new String[0];
        }
        Pattern pattern = Pattern.compile(Pattern.quote(delimiter));
        String[] parts = pattern.split(value);

        for (int i = 0; i < parts.length; i++) {
            parts[i] = decodeString(parts[i]);
        }

        return parts;
    }

    private static String decodeString(String value) {
        if (value == null)
            return null;
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }

    private static MethodArgumentNotValidException getMethodArgumentNotValidException(final ErrorHolder errorHolder) {
        return new MethodArgumentNotValidException(errorHolder.methodParameter(), errorHolder.bindingResult());
    }

    /**
     * Throws an {@link InvalidQueryParameterException} if the provided {@link ErrorHolder} contains errors.
     * This is intended for validation failures that are considered client-side errors (e.g., bad input).
     *
     * @param errorHolder The holder containing the binding result to check.
     * @throws InvalidQueryParameterException if errors are present.
     */
    public static void throwClientSideExceptionIfInvalid(final ErrorHolder errorHolder) {
        if (!errorHolder.bindingResult().hasErrors())
            return;

        throw new InvalidQueryParameterException(
            getMethodArgumentNotValidException(errorHolder)
        );
    }

    /**
     * Throws a {@link QueryBuilderConfigurationException} if the provided {@link ErrorHolder} contains errors.
     * This is intended for validation failures that are considered server-side configuration errors.
     *
     * @param errorHolder The holder containing the binding result to check.
     * @throws QueryBuilderConfigurationException if errors are present.
     */
    public static void throwServerSideExceptionIfInvalid(final ErrorHolder errorHolder) {
        if (!errorHolder.bindingResult().hasErrors())
            return;

        throw new QueryBuilderConfigurationException(
            getMethodArgumentNotValidException(errorHolder)
        );
    }

    /**
     * Checks if a list contains any null elements.
     *
     * @param values The list to check.
     * @return {@code true} if the list contains at least one null, {@code false} otherwise.
     */
    public static boolean isContainNulls(List<?> values) {
        return values.stream().anyMatch(Objects::isNull);
    }

    /**
     * Checks if a list is empty.
     *
     * @param values The list to check.
     * @return {@code true} if the list is empty, {@code false} otherwise.
     */
    public static boolean isEmpty(List<?> values) {
        return values.isEmpty();
    }

    /**
     * Checks if a list is empty or contains any null elements.
     *
     * @param values The list to check.
     * @return {@code true} if the list is empty or contains nulls, {@code false} otherwise.
     */
    public static boolean isNotValidList(List<?> values) {
        return isEmpty(values) || isContainNulls(values);
    }
}
