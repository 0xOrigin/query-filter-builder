package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.ZonedDateTime;

public final class ZonedDateTimeFilter extends AbstractTemporalFilterField<ZonedDateTime> {

    @Override
    public ZonedDateTime cast(Object value) {
        return ZonedDateTime.parse(value.toString());
    }

    @Override
    public Class<ZonedDateTime> getDataType() {
        return ZonedDateTime.class;
    }
}
