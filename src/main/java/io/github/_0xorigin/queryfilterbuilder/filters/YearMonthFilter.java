package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.YearMonth;

public final class YearMonthFilter extends AbstractTemporalFilterField<YearMonth> {

    @Override
    public YearMonth cast(Object value) {
        return YearMonth.parse(value.toString());
    }

    @Override
    public Class<YearMonth> getDataType() {
        return YearMonth.class;
    }
}
