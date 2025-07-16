package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractNumberFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

public class FloatFilter extends AbstractNumberFilterField<Float> {

    @Override
    public Float cast(Object value, ErrorWrapper errorWrapper) {
        return Float.parseFloat(value.toString());
    }

    @Override
    public Class<Float> getDataType() {
        return Float.class;
    }
}
