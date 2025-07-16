package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

public class OffsetDateTimeFilter extends AbstractTemporalFilterField<OffsetDateTime> {

    @Override
    public OffsetDateTime cast(Object value, ErrorWrapper errorWrapper) {
        return OffsetDateTime.parse(value.toString());
    }

    @Override
    public Class<OffsetDateTime> getDataType() {
        return OffsetDateTime.class;
    }
}
