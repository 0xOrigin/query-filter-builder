package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.Year;

public final class YearFilter extends AbstractTemporalFilterField<Year> {

    @Override
    public Year cast(Object value) {
        return Year.parse(value.toString());
    }

    @Override
    public Class<Year> getDataType() {
        return Year.class;
    }
}
