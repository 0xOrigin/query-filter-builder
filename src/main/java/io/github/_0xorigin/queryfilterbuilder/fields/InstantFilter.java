package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.Instant;

public final class InstantFilter extends AbstractTemporalFilterField<Instant> {

    @Override
    public Instant cast(Object value) {
        return Instant.parse(value.toString());
    }

    @Override
    public Class<Instant> getDataType() {
        return Instant.class;
    }
}
