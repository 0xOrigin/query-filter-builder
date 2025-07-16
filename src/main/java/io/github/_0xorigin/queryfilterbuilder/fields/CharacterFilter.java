package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.Operator;

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
        return value.toString().charAt(0);
    }

    @Override
    public Class<Character> getDataType() {
        return Character.class;
    }
}
