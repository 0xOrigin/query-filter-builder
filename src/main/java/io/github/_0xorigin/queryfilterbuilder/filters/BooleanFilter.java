package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;

/**
 * A filter implementation for handling {@link Boolean} fields.
 */
public final class BooleanFilter extends AbstractFilterField<Boolean> {

    /**
     * Constructs the filter and sets the supported operators for boolean types (EQ, NEQ, IS_NULL, IS_NOT_NULL).
     */
    public BooleanFilter() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.IS_NULL, Operator.IS_NOT_NULL
            )
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Casts the string "null" to a Java {@code null}, otherwise uses {@link Boolean#valueOf(String)}.
     */
    @Override
    public Boolean cast(String value) {
        return !value.equals("null") ? Boolean.valueOf(value) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Boolean> getDataType() {
        return Boolean.class;
    }
}
