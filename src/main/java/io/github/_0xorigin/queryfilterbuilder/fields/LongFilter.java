package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractNumberFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

public class LongFilter extends AbstractNumberFilterField<Long> {

    @Override
    public Long cast(Object value, ErrorWrapper errorWrapper) {
        return Long.parseLong(value.toString());
    }

    @Override
    public Class<Long> getDataType() {
        return Long.class;
    }
}
