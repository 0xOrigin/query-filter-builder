package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalDateTime;

public final class LocalDateTimeFilter extends AbstractTemporalFilterField<LocalDateTime> {

    @Override
    public LocalDateTime cast(Object value) {
        return LocalDateTime.parse(value.toString());
    }

    @Override
    public Class<LocalDateTime> getDataType() {
        return LocalDateTime.class;
    }
}
