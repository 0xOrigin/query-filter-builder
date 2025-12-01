package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for all available {@link FilterOperator} implementations.
 * This class holds a map of {@link Operator} constants to their corresponding handlers, allowing for easy lookup.
 */
public final class FilterOperatorRegistry {

    private final Map<Operator, FilterOperator> operatorMap = new EnumMap<>(Operator.class);

    /**
     * Constructs a new registry and populates it with the provided list of filter operators.
     *
     * @param operators A list of {@code FilterOperator} beans, typically injected by Spring.
     */
    public FilterOperatorRegistry(List<FilterOperator> operators) {
        operators.forEach(operator -> addOperator(operator.getOperatorConstant(), operator));
    }

    /**
     * Adds a filter operator to the registry.
     *
     * @param operator        The operator constant.
     * @param filterOperator The filter operator implementation.
     */
    private void addOperator(Operator operator, FilterOperator filterOperator) {
        operatorMap.put(operator, filterOperator);
    }

    /**
     * Retrieves the {@link FilterOperator} implementation for a given {@link Operator} constant.
     *
     * @param operator The {@code Operator} constant.
     * @return The corresponding {@code FilterOperator}, or {@code null} if no handler is registered for the operator.
     */
    public FilterOperator getOperator(Operator operator) {
        return operatorMap.get(operator);
    }

    /**
     * Gets a view of the registered filter operators.
     *
     * @return An unmodifiable map of operator constants to filter operator handlers.
     */
    public Map<Operator, FilterOperator> getOperators() {
        return Collections.unmodifiableMap(operatorMap);
    }
}
