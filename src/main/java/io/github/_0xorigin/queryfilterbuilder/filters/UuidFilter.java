package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;
import java.util.UUID;

/**
 * A filter implementation for handling {@link UUID} fields.
 */
public final class UuidFilter extends AbstractFilterField<UUID> {

    /**
     * Constructs the filter and sets the supported operators for UUID types.
     */
    public UuidFilter() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID cast(String value) {
        return UUID.fromString(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<UUID> getDataType() {
        return UUID.class;
    }
}
