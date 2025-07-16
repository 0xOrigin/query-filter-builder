package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractTemporalFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public class InstantFilter extends AbstractTemporalFilterField<Instant> {

    @Override
    public Instant cast(Object value, ErrorWrapper errorWrapper) {
        return Instant.parse(value.toString());
    }

    @Override
    public Class<Instant> getDataType() {
        return Instant.class;
    }
}
