package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractNumberFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.FilterUtils;

public class ByteFilter extends AbstractNumberFilterField<Byte> {

    @Override
    public Byte cast(Object value, ErrorWrapper errorWrapper) {
        return Byte.parseByte(value.toString());
    }

    @Override
    public Class<Byte> getDataType() {
        return Byte.class;
    }
}
