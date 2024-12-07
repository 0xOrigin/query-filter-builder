package io.github._0xorigin.fields;

import io.github._0xorigin.base.ErrorWrapper;
import io.github._0xorigin.base.Operator;
import io.github._0xorigin.base.AbstractFilterField;

import java.util.Set;
import java.util.UUID;

public class UuidFilter extends AbstractFilterField<UUID> {

    {
        this.setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }

    @Override
    public UUID cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return UUID.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    value.toString(),
                    e.getLocalizedMessage()
                )
            );
        }

        return null;
    }

}
