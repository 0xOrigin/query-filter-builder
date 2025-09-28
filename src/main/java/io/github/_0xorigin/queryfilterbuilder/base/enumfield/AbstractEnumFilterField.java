package io.github._0xorigin.queryfilterbuilder.base.enumfield;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.OperatorSupport;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Set;

/**
 * An abstract base class that provides support for managing a set of operators for enum type.
 */
public abstract class AbstractEnumFilterField extends OperatorSupport implements EnumFilterField, EnumFieldCaster {

    /**
     * Constructs the filter and sets the supported operators for enum type,
     * including comparison, pattern matching, and null checks.
     */
    protected AbstractEnumFilterField() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN,
                Operator.CONTAINS, Operator.ICONTAINS, Operator.STARTS_WITH, Operator.ISTARTS_WITH, Operator.ENDS_WITH, Operator.IENDS_WITH
            )
        );
    }
}
