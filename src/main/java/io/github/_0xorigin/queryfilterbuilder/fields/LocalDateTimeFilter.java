package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class LocalDateTimeFilter extends AbstractTemporalFilterField<LocalDateTime> {

    @Override
    public LocalDateTime cast(Object value, ErrorWrapper errorWrapper) {
        return LocalDateTime.parse(value.toString());
    }

    @Override
    public Class<LocalDateTime> getDataType() {
        return LocalDateTime.class;
    }
}
