package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractTemporalFilterField;
import io.github._0xorigin.base.ErrorWrapper;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class LocalTimeFilter extends AbstractTemporalFilterField<LocalTime> {

    @Override
    public LocalTime cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return LocalTime.parse(value.toString());
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
