package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

import java.math.BigInteger;

public final class BigIntegerFilter extends AbstractNumberFilterField<BigInteger> {

    @Override
    public BigInteger cast(String value) {
        return new BigInteger(value);
    }

    @Override
    public Class<BigInteger> getDataType() {
        return BigInteger.class;
    }
}
