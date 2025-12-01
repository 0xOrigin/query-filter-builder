package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

/**
 * A filter implementation for handling {@link Short} fields.
 */
public final class ShortFilter extends AbstractNumberFilterField<Short> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Short cast(String value) {
        return Short.parseShort(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Short> getDataType() {
        return Short.class;
    }
}
