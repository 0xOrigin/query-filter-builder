package io.github._0xorigin.queryfilterbuilder.filters;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;

/**
 * A filter implementation for handling {@link String} fields.
 */
public final class StringFilter extends AbstractFilterField<String> {

    /**
     * Constructs the filter and sets the supported operators for string types,
     * including comparison, pattern matching, and null checks.
     */
    public StringFilter() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN,
                Operator.CONTAINS, Operator.ICONTAINS, Operator.STARTS_WITH, Operator.ISTARTS_WITH, Operator.ENDS_WITH, Operator.IENDS_WITH
            )
        );
    }

    /**
     * {@inheritDoc}
     * <p>
     * The input string is returned as is.
     */
    @Override
    public String cast(String value) {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getDataType() {
        return String.class;
    }
}
