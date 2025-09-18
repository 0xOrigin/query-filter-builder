package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

/**
 * A filter implementation for handling {@link Byte} fields.
 */
public final class ByteFilter extends AbstractNumberFilterField<Byte> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte cast(String value) {
        return Byte.parseByte(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Byte> getDataType() {
        return Byte.class;
    }
}
