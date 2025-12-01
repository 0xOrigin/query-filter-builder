package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalTime;

/**
 * A filter implementation for handling {@link LocalTime} fields.
 */
public final class LocalTimeFilter extends AbstractTemporalFilterField<LocalTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalTime cast(String value) {
        return LocalTime.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LocalTime> getDataType() {
        return LocalTime.class;
    }
}
