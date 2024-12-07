package io.github._0xorigin.fields;

import io.github._0xorigin.base.AbstractFilterField;
import io.github._0xorigin.base.ErrorWrapper;
import io.github._0xorigin.base.Operator;

import java.util.Set;

public class CharacterFilter extends AbstractFilterField<Character> {

    {
        this.setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN
            )
        );
    }

    @Override
    public Character cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return value.toString().charAt(0);
        } catch (Exception e) {
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
