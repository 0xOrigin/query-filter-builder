package io.github._0xorigin;

import io.github._0xorigin.base.*;
import io.github._0xorigin.registries.FilterRegistry;

import java.util.*;

public class FilterContext<T> {

    private final Map<String, Set<Operator>> fieldOperators = new HashMap<>();
    private final Map<String, CustomFilterWrapper<T>> customFieldFilters = new HashMap<>();

    public FilterContext<T> addFilter(
            String fieldName,
            Operator... operators
    ) {
        fieldOperators.put(fieldName, new HashSet<>(Arrays.asList(operators)));
        return this;
    }

    public FilterContext<T> addFilter(
            String fieldName,
            Class<? extends Comparable<?>> dataType,
            CustomFilterFunction<T> filterFunction
    ) {
        AbstractFilterField<?> filterField = FilterRegistry.getFieldFilter(dataType);
        customFieldFilters.put(fieldName, new CustomFilterWrapper<T>(filterField, filterFunction));
        return this;
    }

    public Map<String, Set<Operator>> getFieldOperators() {
        return fieldOperators;
    }

    public Map<String, CustomFilterWrapper<T>> getCustomFieldFilters() {
        return customFieldFilters;
    }

}
