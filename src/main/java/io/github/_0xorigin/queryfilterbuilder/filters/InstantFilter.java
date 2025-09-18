package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.Instant;

/**
 * A filter implementation for handling {@link Instant} fields.
 */
public final class InstantFilter extends AbstractTemporalFilterField<Instant> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Instant cast(String value) {
        return Instant.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Instant> getDataType() {
        return Instant.class;
    }
}
