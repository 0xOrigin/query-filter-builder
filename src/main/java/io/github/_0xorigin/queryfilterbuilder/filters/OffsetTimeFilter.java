package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.OffsetTime;

/**
 * A filter implementation for handling {@link OffsetTime} fields.
 */
public final class OffsetTimeFilter extends AbstractTemporalFilterField<OffsetTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public OffsetTime cast(String value) {
        return OffsetTime.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<OffsetTime> getDataType() {
        return OffsetTime.class;
    }
}
