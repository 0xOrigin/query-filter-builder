package com.github._0xorigin.base;

import java.util.List;

public class FilterWrapper {

    private final String field;
    private final Operator operator;
    private final List<?> values;
    private final String originalFieldName;

    public FilterWrapper(String field, String originalFieldName, Operator operator, List<?> values) {
        this.field = field;
        this.originalFieldName = originalFieldName;
        this.operator = operator;
        this.values = values;
    }

    public String getField() {
        return field;
    }

    public String getOriginalFieldName() {
        return originalFieldName;
    }

    public Operator getOperator() {
        return operator;
    }

    public List<?> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "FilterWrapper{" +
                "field='" + field + '\'' +
                ", originalFieldName='" + originalFieldName + '\'' +
                ", operator=" + operator +
                ", values=" + values +
                '}';
    }

}
