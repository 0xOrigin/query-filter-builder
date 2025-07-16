package io.github._0xorigin.queryfilterbuilder.base;

import java.io.Serializable;
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

    @SuppressWarnings("unchecked")
    public <K extends Comparable<? super K> & Serializable> List<K> getValues() {
        return (List<K>) values;
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
