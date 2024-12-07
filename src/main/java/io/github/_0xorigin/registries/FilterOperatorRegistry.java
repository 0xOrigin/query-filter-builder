package io.github._0xorigin.registries;

import io.github._0xorigin.base.Operator;
import io.github._0xorigin.operators.*;
import io.github._0xorigin.base.FilterOperator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilterOperatorRegistry {

    private static final Map<Operator, FilterOperator> operatorMap = new ConcurrentHashMap<>();

    static {
        addOperator(Operator.EQ, new Equal());
        addOperator(Operator.NEQ, new NotEqual());
        addOperator(Operator.IS_NULL, new IsNull());
        addOperator(Operator.IS_NOT_NULL, new IsNotNull());
        addOperator(Operator.GT, new GreaterThan());
        addOperator(Operator.LT, new LessThan());
        addOperator(Operator.GTE, new GreaterThanEqual());
        addOperator(Operator.LTE, new LessThanEqual());
        addOperator(Operator.CONTAINS, new Contains());
        addOperator(Operator.ICONTAINS, new IContains());
        addOperator(Operator.STARTS_WITH, new StartsWith());
        addOperator(Operator.ISTARTS_WITH, new IStartsWith());
        addOperator(Operator.ENDS_WITH, new EndsWith());
        addOperator(Operator.IENDS_WITH, new IEndsWith());
        addOperator(Operator.IN, new In());
        addOperator(Operator.NOT_IN, new NotIn());
        addOperator(Operator.BETWEEN, new Between());
        addOperator(Operator.NOT_BETWEEN, new NotBetween());
    }

    private static synchronized void addOperator(Operator operator, FilterOperator filterOperator) {
        operatorMap.put(operator, filterOperator);
    }

    public static synchronized FilterOperator getOperator(Operator operator) {
        return operatorMap.get(operator);
    }

    public static synchronized Map<Operator, FilterOperator> getAllOperators() {
        return operatorMap;
    }

}
