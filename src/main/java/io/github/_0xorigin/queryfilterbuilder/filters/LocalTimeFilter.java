package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalTime;

public final class LocalTimeFilter extends AbstractTemporalFilterField<LocalTime> {

    @Override
    public LocalTime cast(String value) {
        return LocalTime.parse(value);
    }

    @Override
    public Class<LocalTime> getDataType() {
        return LocalTime.class;
    }
}
