package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.OffsetDateTime;

public final class OffsetDateTimeFilter extends AbstractTemporalFilterField<OffsetDateTime> {

    @Override
    public OffsetDateTime cast(Object value) {
        return OffsetDateTime.parse(value.toString());
    }

    @Override
    public Class<OffsetDateTime> getDataType() {
        return OffsetDateTime.class;
    }
}
