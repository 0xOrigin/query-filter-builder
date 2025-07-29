package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class LongFilter extends AbstractNumberFilterField<Long> {

    @Override
    public Long cast(Object value) {
        return Long.parseLong(value.toString());
    }

    @Override
    public Class<Long> getDataType() {
        return Long.class;
    }
}
