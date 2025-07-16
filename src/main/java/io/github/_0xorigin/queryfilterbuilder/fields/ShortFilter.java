package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractNumberFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

public class ShortFilter extends AbstractNumberFilterField<Short> {

    @Override
    public Short cast(Object value, ErrorWrapper errorWrapper) {
        return Short.parseShort(value.toString());
    }

    @Override
    public Class<Short> getDataType() {
        return Short.class;
    }
}
