package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.io.Serializable;
import java.time.temporal.Temporal;
import java.util.Set;

/**
 * An abstract base class for filter fields that handle temporal types (e.g., dates, times).
 * It automatically configures a standard set of operators applicable to temporals (e.g., EQ, GT, BETWEEN).
 *
 * @param <T> The specific {@link Temporal} type (e.g., LocalDate, OffsetDateTime).
 */
public abstract class AbstractTemporalFilterField<T extends Temporal & Comparable<? super T> & Serializable> extends AbstractFilterField<T> {

    /**
     * Constructs the filter field and sets the default supported operators for temporal types.
     */
    protected AbstractTemporalFilterField() {
        setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }
}
