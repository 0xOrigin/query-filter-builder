package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

/**
 * A filter implementation for handling {@link Long} fields.
 */
public final class LongFilter extends AbstractNumberFilterField<Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Long cast(String value) {
        return Long.parseLong(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Long> getDataType() {
        return Long.class;
    }
}
