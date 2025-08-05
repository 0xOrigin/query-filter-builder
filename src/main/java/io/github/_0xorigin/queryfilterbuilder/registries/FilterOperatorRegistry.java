package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class FilterOperatorRegistry {

    private final Map<Operator, FilterOperator> operatorMap = new EnumMap<>(Operator.class);

    public FilterOperatorRegistry(List<FilterOperator> operators) {
        operators.forEach(operator -> addOperator(operator.getOperatorConstant(), operator));
    }

    private void addOperator(Operator operator, FilterOperator filterOperator) {
        operatorMap.put(operator, filterOperator);
    }

    public FilterOperator getOperator(Operator operator) {
        return operatorMap.get(operator);
    }
}
