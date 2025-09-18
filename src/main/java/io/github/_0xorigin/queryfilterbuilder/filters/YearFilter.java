package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.Year;

/**
 * A filter implementation for handling {@link Year} fields.
 */
public final class YearFilter extends AbstractTemporalFilterField<Year> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Year cast(String value) {
        return Year.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Year> getDataType() {
        return Year.class;
    }
}
