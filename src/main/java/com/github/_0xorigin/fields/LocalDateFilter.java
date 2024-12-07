package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractTemporalFilterField;
import com.github._0xorigin.base.ErrorWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateFilter extends AbstractTemporalFilterField<LocalDate> {

    @Override
    public LocalDate cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return LocalDate.parse(value.toString());
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
