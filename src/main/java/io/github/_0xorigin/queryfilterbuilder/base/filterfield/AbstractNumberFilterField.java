package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.io.Serializable;
import java.util.Set;

/**
 * An abstract base class for filter fields that handle numeric types.
 * It automatically configures a standard set of operators applicable to numbers (e.g., EQ, GT, BETWEEN).
 *
 * @param <T> The specific {@link Number} type (e.g., Integer, BigDecimal).
 */
public abstract class AbstractNumberFilterField<T extends Number & Comparable<? super T> & Serializable> extends AbstractFilterField<T> {

    /**
     * Constructs the filter field and sets the default supported operators for numeric types.
     */
    protected AbstractNumberFilterField() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }
}
