package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

/**
 * A filter implementation for handling {@link Double} fields.
 */
public final class DoubleFilter extends AbstractNumberFilterField<Double> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Double cast(String value) {
        return Double.parseDouble(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Double> getDataType() {
        return Double.class;
    }
}
