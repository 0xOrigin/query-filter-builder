package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

/**
 * A filter implementation for handling {@link Float} fields.
 */
public final class FloatFilter extends AbstractNumberFilterField<Float> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Float cast(String value) {
        return Float.parseFloat(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Float> getDataType() {
        return Float.class;
    }
}
