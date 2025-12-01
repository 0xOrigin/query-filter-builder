package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

import java.math.BigInteger;

/**
 * A filter implementation for handling {@link BigInteger} fields.
 */
public final class BigIntegerFilter extends AbstractNumberFilterField<BigInteger> {

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger cast(String value) {
        return new BigInteger(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<BigInteger> getDataType() {
        return BigInteger.class;
    }
}
