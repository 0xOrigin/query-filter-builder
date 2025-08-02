package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalDate;

public final class LocalDateFilter extends AbstractTemporalFilterField<LocalDate> {

    @Override
    public LocalDate cast(String value) {
        return LocalDate.parse(value);
    }

    @Override
    public Class<LocalDate> getDataType() {
        return LocalDate.class;
    }
}
