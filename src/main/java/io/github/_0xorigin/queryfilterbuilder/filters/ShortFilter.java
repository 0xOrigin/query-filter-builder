package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class ShortFilter extends AbstractNumberFilterField<Short> {

    @Override
    public Short cast(String value) {
        return Short.parseShort(value);
    }

    @Override
    public Class<Short> getDataType() {
        return Short.class;
    }
}
