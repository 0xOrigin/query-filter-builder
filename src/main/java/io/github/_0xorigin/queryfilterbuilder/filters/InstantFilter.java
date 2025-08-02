package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractTemporalFilterField;

import java.time.Instant;

public final class InstantFilter extends AbstractTemporalFilterField<Instant> {

    @Override
    public Instant cast(String value) {
        return Instant.parse(value);
    }

    @Override
    public Class<Instant> getDataType() {
        return Instant.class;
    }
}
