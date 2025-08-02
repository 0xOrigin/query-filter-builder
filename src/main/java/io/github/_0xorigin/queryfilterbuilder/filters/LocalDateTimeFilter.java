package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalDateTime;

public final class LocalDateTimeFilter extends AbstractTemporalFilterField<LocalDateTime> {

    @Override
    public LocalDateTime cast(String value) {
        return LocalDateTime.parse(value);
    }

    @Override
    public Class<LocalDateTime> getDataType() {
        return LocalDateTime.class;
    }
}
