package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class DoubleFilter extends AbstractNumberFilterField<Double> {

    @Override
    public Double cast(Object value) {
        return Double.parseDouble(value.toString());
    }

    @Override
    public Class<Double> getDataType() {
        return Double.class;
    }
}
