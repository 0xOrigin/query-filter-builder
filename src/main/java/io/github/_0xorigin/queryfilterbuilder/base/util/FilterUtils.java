package io.github._0xorigin.queryfilterbuilder.base.util;

import io.github._0xorigin.queryfilterbuilder.base.wrapper.ErrorWrapper;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.Objects;

public final class FilterUtils {

    private FilterUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static FieldError generateFieldError(
            ErrorWrapper errorWrapper,
            String value,
            boolean bindingFailure,
            @Nullable String[] codes,
            @Nullable Object[] arguments,
            @Nullable String defaultMessage
    ) {
        return new FieldError(
            errorWrapper.bindingResult().getObjectName(),
            errorWrapper.filterWrapper().originalFieldName(),
            value,
            bindingFailure,
            codes,
            arguments,
            defaultMessage
        );
    }

    public static FieldError generateFieldError(ErrorWrapper errorWrapper, String value, String defaultMessage) {
        return generateFieldError(
            errorWrapper,
            value,
            false,
            null,
            null,
            defaultMessage
        );
    }

    public static FieldError generateFieldError(ErrorWrapper errorWrapper, String value, String defaultMessage, String messagePrefix) {
        return generateFieldError(
            errorWrapper,
            value,
            false,
            null,
            null,
            messagePrefix + defaultMessage
        );
    }

    public static void addError(ErrorWrapper errorWrapper, ObjectError error) {
        errorWrapper
            .bindingResult()
            .addError(error);
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
