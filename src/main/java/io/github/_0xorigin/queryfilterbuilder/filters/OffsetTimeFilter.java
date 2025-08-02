package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.OffsetTime;

public final class OffsetTimeFilter extends AbstractTemporalFilterField<OffsetTime> {

    @Override
    public OffsetTime cast(String value) {
        return OffsetTime.parse(value);
    }

    @Override
    public Class<OffsetTime> getDataType() {
        return OffsetTime.class;
    }
}
