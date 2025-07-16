package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateFilter extends AbstractTemporalFilterField<LocalDate> {

    @Override
    public LocalDate cast(Object value, ErrorWrapper errorWrapper) {
        return LocalDate.parse(value.toString());
    }

    @Override
    public Class<LocalDate> getDataType() {
        return LocalDate.class;
    }
}
