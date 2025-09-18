package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.OffsetDateTime;

/**
 * A filter implementation for handling {@link OffsetDateTime} fields.
 */
public final class OffsetDateTimeFilter extends AbstractTemporalFilterField<OffsetDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public OffsetDateTime cast(String value) {
        return OffsetDateTime.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<OffsetDateTime> getDataType() {
        return OffsetDateTime.class;
    }
}
