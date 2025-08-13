package io.github._0xorigin.queryfilterbuilder.base.utils;

import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.exceptions.QueryBuilderConfigurationException;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryParameterException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class FilterUtils {

    private FilterUtils() {
        throw new IllegalStateException("Utility class");
    }

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

    public static FieldError generateFieldError(BindingResult bindingResult, String fieldName, String value, String defaultMessage) {
        return generateFieldError(bindingResult, fieldName, value, false, null, null, defaultMessage);
    }

    public static void addFieldError(BindingResult bindingResult, String fieldName, String value, String defaultMessage) {
        bindingResult.addError(generateFieldError(bindingResult, fieldName, value, defaultMessage));
    }

    public static void addError(BindingResult bindingResult, ObjectError objectError) {
        bindingResult.addError(objectError);
    }

    public static String[] splitWithEscapedDelimiter(String value, String delimiter) {
        Pattern pattern = Pattern.compile(Pattern.quote(delimiter));
        return pattern.split(value);
    }

    private static MethodArgumentNotValidException getMethodArgumentNotValidException(final ErrorHolder errorHolder) {
        return new MethodArgumentNotValidException(errorHolder.methodParameter(), errorHolder.bindingResult());
    }

    public static void throwClientSideExceptionIfInvalid(final ErrorHolder errorHolder) {
        if (!errorHolder.bindingResult().hasErrors())
            return;

        throw new InvalidQueryParameterException(
            getMethodArgumentNotValidException(errorHolder)
        );
    }

    public static void throwServerSideExceptionIfInvalid(final ErrorHolder errorHolder) {
        if (!errorHolder.bindingResult().hasErrors())
            return;

        throw new QueryBuilderConfigurationException(
            getMethodArgumentNotValidException(errorHolder)
        );
    }

    public static boolean isContainNulls(List<?> values) {
        return values.stream().anyMatch(Objects::isNull);
    }

    public static boolean isEmpty(List<?> values) {
        return values.isEmpty();
    }

    public static boolean isNotValidList(List<?> values) {
        return isEmpty(values) || isContainNulls(values);
    }
}
