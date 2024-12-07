package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractTemporalFilterField;
import io.github._0xorigin.base.ErrorWrapper;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class YearMonthFilter extends AbstractTemporalFilterField<YearMonth> {

    @Override
    public YearMonth cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return YearMonth.parse(value.toString());
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
