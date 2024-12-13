package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.Operator;
import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterField;

import java.util.Set;

public class BooleanFilter extends AbstractFilterField<Boolean> {

    {
        this.setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.IS_NULL, Operator.IS_NOT_NULL
            )
        );
    }

    @Override
    public Boolean cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return !value.equals("null") ? Boolean.valueOf(value.toString()) : null;
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
