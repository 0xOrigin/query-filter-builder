package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.LocalDate;

/**
 * A filter implementation for handling {@link LocalDate} fields.
 */
public final class LocalDateFilter extends AbstractTemporalFilterField<LocalDate> {

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDate cast(String value) {
        return LocalDate.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<LocalDate> getDataType() {
        return LocalDate.class;
    }
}
