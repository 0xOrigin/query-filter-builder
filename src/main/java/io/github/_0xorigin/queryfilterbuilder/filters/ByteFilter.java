package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class ByteFilter extends AbstractNumberFilterField<Byte> {

    @Override
    public Byte cast(String value) {
        return Byte.parseByte(value);
    }

    @Override
    public Class<Byte> getDataType() {
        return Byte.class;
    }
}
