package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class ByteFilter extends AbstractNumberFilterField<Byte> {

    @Override
    public Byte cast(Object value) {
        return Byte.parseByte(value.toString());
    }

    @Override
    public Class<Byte> getDataType() {
        return Byte.class;
    }
}
