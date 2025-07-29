package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class ShortFilter extends AbstractNumberFilterField<Short> {

    @Override
    public Short cast(Object value) {
        return Short.parseShort(value.toString());
    }

    @Override
    public Class<Short> getDataType() {
        return Short.class;
    }
}
