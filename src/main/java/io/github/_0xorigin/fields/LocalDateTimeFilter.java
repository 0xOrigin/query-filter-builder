package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractTemporalFilterField;
import io.github._0xorigin.base.ErrorWrapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeFilter extends AbstractTemporalFilterField<LocalDateTime> {

    @Override
    public LocalDateTime cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return LocalDateTime.parse(value.toString());
        } catch (DateTimeParseException e) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    value.toString(),
                    e.getLocalizedMessage()
                )
            );
        }

        return null;
    }

}
