package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractTemporalFilterField;
import io.github._0xorigin.base.ErrorWrapper;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class OffsetTimeFilter extends AbstractTemporalFilterField<OffsetDateTime> {

    @Override
    public OffsetDateTime cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return OffsetDateTime.parse(value.toString());
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
