package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class IntegerFilter extends AbstractNumberFilterField<Integer> {

    @Override
    public Integer cast(Object value) {
        return Integer.parseInt(value.toString());
    }

    @Override
    public Class<Integer> getDataType() {
        return Integer.class;
    }
}
