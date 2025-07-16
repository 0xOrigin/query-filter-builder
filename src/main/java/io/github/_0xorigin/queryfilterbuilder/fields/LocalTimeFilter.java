package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class LocalTimeFilter extends AbstractTemporalFilterField<LocalTime> {

    @Override
    public LocalTime cast(Object value, ErrorWrapper errorWrapper) {
        return LocalTime.parse(value.toString());
    }

    @Override
    public Class<LocalTime> getDataType() {
        return LocalTime.class;
    }
}
