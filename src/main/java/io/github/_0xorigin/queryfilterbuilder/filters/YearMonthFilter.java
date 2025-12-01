package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.YearMonth;

/**
 * A filter implementation for handling {@link YearMonth} fields.
 */
public final class YearMonthFilter extends AbstractTemporalFilterField<YearMonth> {

    /**
     * {@inheritDoc}
     */
    @Override
    public YearMonth cast(String value) {
        return YearMonth.parse(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<YearMonth> getDataType() {
        return YearMonth.class;
    }
}
