package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

/**
 * A filter implementation for handling {@link Integer} fields.
 */
public final class IntegerFilter extends AbstractNumberFilterField<Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer cast(String value) {
        return Integer.parseInt(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Integer> getDataType() {
        return Integer.class;
    }
}
