package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.ZonedDateTime;

public final class ZonedDateTimeFilter extends AbstractTemporalFilterField<ZonedDateTime> {

    @Override
    public ZonedDateTime cast(String value) {
        return ZonedDateTime.parse(value);
    }

    @Override
    public Class<ZonedDateTime> getDataType() {
        return ZonedDateTime.class;
    }
}
