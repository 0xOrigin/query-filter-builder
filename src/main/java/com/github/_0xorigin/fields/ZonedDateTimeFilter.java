package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractTemporalFilterField;
import com.github._0xorigin.base.ErrorWrapper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeFilter extends AbstractTemporalFilterField<ZonedDateTime> {

    @Override
    public ZonedDateTime cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return ZonedDateTime.parse(value.toString());
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
