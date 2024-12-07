package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractTemporalFilterField;
import io.github._0xorigin.base.ErrorWrapper;

import java.time.Year;
import java.time.format.DateTimeParseException;

public class YearFilter extends AbstractTemporalFilterField<Year> {

    @Override
    public Year cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Year.parse(value.toString());
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