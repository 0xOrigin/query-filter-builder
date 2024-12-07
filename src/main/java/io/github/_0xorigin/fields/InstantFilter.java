package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractTemporalFilterField;
import io.github._0xorigin.base.ErrorWrapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public class InstantFilter extends AbstractTemporalFilterField<Instant> {

    @Override
    public Instant cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Instant.parse(value.toString());
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