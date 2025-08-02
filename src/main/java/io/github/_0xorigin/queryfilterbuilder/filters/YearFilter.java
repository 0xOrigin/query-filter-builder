package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.Year;

public final class YearFilter extends AbstractTemporalFilterField<Year> {

    @Override
    public Year cast(String value) {
        return Year.parse(value);
    }

    @Override
    public Class<Year> getDataType() {
        return Year.class;
    }
}
