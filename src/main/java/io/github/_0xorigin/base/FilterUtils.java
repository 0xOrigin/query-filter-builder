package io.github._0xorigin.base;

import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public abstract class FilterUtils {

    protected FieldError generateFieldError(
            ErrorWrapper errorWrapper,
            String value,
            boolean bindingFailure,
            @Nullable String[] codes,
            @Nullable Object[] arguments,
            @Nullable String defaultMessage
    ) {
        return new FieldError(
                errorWrapper.getBindingResult().getObjectName(),
                errorWrapper.getFilterWrapper().getOriginalFieldName(),
                value,
                bindingFailure,
                codes,
                arguments,
                defaultMessage
        );
    }

    protected FieldError generateFieldError(ErrorWrapper errorWrapper, String value, String defaultMessage) {
        return generateFieldError(
                errorWrapper,
                value,
                false,
                null,
                null,
                defaultMessage
        );
    }

    protected FieldError generateFieldError(ErrorWrapper errorWrapper, String value, String defaultMessage, String messagePrefix) {
        return generateFieldError(
                errorWrapper,
                value,
                false,
                null,
                null,
                messagePrefix + defaultMessage
        );
    }

    protected void addError(ErrorWrapper errorWrapper, ObjectError error) {
        errorWrapper
            .getBindingResult()
            .addError(error);
    }

}
