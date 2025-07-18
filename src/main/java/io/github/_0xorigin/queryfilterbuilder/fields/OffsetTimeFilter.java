package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.OffsetTime;

public final class OffsetTimeFilter extends AbstractTemporalFilterField<OffsetTime> {

    @Override
    public OffsetTime cast(Object value) {
        return OffsetTime.parse(value.toString());
    }

    @Override
    public Class<OffsetTime> getDataType() {
        return OffsetTime.class;
    }
}
