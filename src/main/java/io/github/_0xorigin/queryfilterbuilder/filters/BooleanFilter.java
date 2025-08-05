package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;

public final class BooleanFilter extends AbstractFilterField<Boolean> {

    public BooleanFilter() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.IS_NULL, Operator.IS_NOT_NULL
            )
        );
    }

    @Override
    public Boolean cast(String value) {
        return !value.equals("null") ? Boolean.valueOf(value) : null;
    }

    @Override
    public Class<Boolean> getDataType() {
        return Boolean.class;
    }
}
