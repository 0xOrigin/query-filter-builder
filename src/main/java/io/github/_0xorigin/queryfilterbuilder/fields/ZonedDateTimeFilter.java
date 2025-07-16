package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class ZonedDateTimeFilter extends AbstractTemporalFilterField<ZonedDateTime> {

    @Override
    public ZonedDateTime cast(Object value, ErrorWrapper errorWrapper) {
        return ZonedDateTime.parse(value.toString());
    }

    @Override
    public Class<ZonedDateTime> getDataType() {
        return ZonedDateTime.class;
    }
}
