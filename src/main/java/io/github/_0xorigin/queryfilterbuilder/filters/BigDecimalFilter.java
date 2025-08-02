package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractNumberFilterField;

import java.math.BigDecimal;

public final class BigDecimalFilter extends AbstractNumberFilterField<BigDecimal> {

    @Override
    public BigDecimal cast(String value) {
        return new BigDecimal(value);
    }

    @Override
    public Class<BigDecimal> getDataType() {
        return BigDecimal.class;
    }
}
