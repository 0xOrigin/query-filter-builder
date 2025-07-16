package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractNumberFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

public class DoubleFilter extends AbstractNumberFilterField<Double> {

    @Override
    public Double cast(Object value, ErrorWrapper errorWrapper) {
        return Double.parseDouble(value.toString());
    }

    @Override
    public Class<Double> getDataType() {
        return Double.class;
    }
}
