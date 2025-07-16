package io.github._0xorigin.queryfilterbuilder.fields;

import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.Operator;
import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterField;

import java.util.Set;

public class StringFilter extends AbstractFilterField<String> {

    {
        this.setSupportedOperators(
            Set.of(
                Operator.EQ, Operator.NEQ, Operator.GT, Operator.LT, Operator.GTE, Operator.LTE,
                Operator.IS_NULL, Operator.IS_NOT_NULL, Operator.IN, Operator.NOT_IN, Operator.BETWEEN, Operator.NOT_BETWEEN,
                Operator.CONTAINS, Operator.ICONTAINS, Operator.STARTS_WITH, Operator.ISTARTS_WITH, Operator.ENDS_WITH, Operator.IENDS_WITH
            )
        );
    }

    @Override
    public String cast(Object value, ErrorWrapper errorWrapper) {
        return value.toString();
    }

    @Override
    public Class<String> getDataType() {
        return String.class;
    }
}
