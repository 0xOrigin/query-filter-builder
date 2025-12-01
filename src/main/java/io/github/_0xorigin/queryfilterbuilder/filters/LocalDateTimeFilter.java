package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalDateTime;

/**
 * A filter implementation for handling {@link LocalDateTime} fields.
 */
public final class LocalDateTimeFilter extends AbstractTemporalFilterField<LocalDateTime> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime cast(String value) {
        return LocalDateTime.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LocalDateTime> getDataType() {
        return LocalDateTime.class;
    }
}
