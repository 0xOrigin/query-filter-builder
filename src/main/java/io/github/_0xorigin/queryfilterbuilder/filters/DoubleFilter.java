package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

public final class DoubleFilter extends AbstractNumberFilterField<Double> {

    @Override
    public Double cast(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Class<Double> getDataType() {
        return Double.class;
    }
}
