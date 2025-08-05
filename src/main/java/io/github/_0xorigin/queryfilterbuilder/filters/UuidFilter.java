package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;
import java.util.UUID;

public final class UuidFilter extends AbstractFilterField<UUID> {

    public UuidFilter() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }

    @Override
    public UUID cast(String value) {
        return UUID.fromString(value);
    }

    @Override
    public Class<UUID> getDataType() {
        return UUID.class;
    }
}
