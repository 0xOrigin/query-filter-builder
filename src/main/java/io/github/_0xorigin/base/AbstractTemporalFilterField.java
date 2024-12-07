package io.github._0xorigin.base;

import java.time.temporal.Temporal;
import java.util.Set;

public abstract class AbstractTemporalFilterField<T extends Temporal> extends AbstractFilterField<T> {

    {
        this.setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }

}
