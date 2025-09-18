package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

import java.math.BigDecimal;

/**
 * A filter implementation for handling {@link BigDecimal} fields.
 */
public final class BigDecimalFilter extends AbstractNumberFilterField<BigDecimal> {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal cast(String value) {
        return new BigDecimal(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BigDecimal> getDataType() {
        return BigDecimal.class;
    }
}
