package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.ZonedDateTime;

/**
 * A filter implementation for handling {@link ZonedDateTime} fields.
 */
public final class ZonedDateTimeFilter extends AbstractTemporalFilterField<ZonedDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime cast(String value) {
        return ZonedDateTime.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ZonedDateTime> getDataType() {
        return ZonedDateTime.class;
    }
}
