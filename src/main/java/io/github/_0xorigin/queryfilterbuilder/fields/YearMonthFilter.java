package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class YearMonthFilter extends AbstractTemporalFilterField<YearMonth> {

    @Override
    public YearMonth cast(Object value, ErrorWrapper errorWrapper) {
        return YearMonth.parse(value.toString());
    }

    @Override
    public Class<YearMonth> getDataType() {
        return YearMonth.class;
    }
}
