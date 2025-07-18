package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalTime;

public final class LocalTimeFilter extends AbstractTemporalFilterField<LocalTime> {

    @Override
    public LocalTime cast(Object value) {
        return LocalTime.parse(value.toString());
    }

    @Override
    public Class<LocalTime> getDataType() {
        return LocalTime.class;
    }
}
