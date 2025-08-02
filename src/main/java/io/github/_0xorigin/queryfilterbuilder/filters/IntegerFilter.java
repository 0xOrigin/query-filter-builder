package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class IntegerFilter extends AbstractNumberFilterField<Integer> {

    @Override
    public Integer cast(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public Class<Integer> getDataType() {
        return Integer.class;
    }
}
