package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class FloatFilter extends AbstractNumberFilterField<Float> {

    @Override
    public Float cast(Object value) {
        return Float.parseFloat(value.toString());
    }

    @Override
    public Class<Float> getDataType() {
        return Float.class;
    }
}
