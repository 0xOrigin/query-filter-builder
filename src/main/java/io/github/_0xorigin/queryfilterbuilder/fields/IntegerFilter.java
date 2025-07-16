package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractNumberFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

public class IntegerFilter extends AbstractNumberFilterField<Integer> {

    @Override
    public Integer cast(Object value, ErrorWrapper errorWrapper) {
        return Integer.parseInt(value.toString());
    }

    @Override
    public Class<Integer> getDataType() {
        return Integer.class;
    }
}
