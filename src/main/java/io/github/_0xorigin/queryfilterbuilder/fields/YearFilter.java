package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.Year;
import java.time.format.DateTimeParseException;

public class YearFilter extends AbstractTemporalFilterField<Year> {

    @Override
    public Year cast(Object value, ErrorWrapper errorWrapper) {
        return Year.parse(value.toString());
    }

    @Override
    public Class<Year> getDataType() {
        return Year.class;
    }
}
