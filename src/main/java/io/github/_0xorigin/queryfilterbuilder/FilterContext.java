package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.*;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;

import java.util.*;

public class FilterContext<T> {

    private final Map<String, Set<Operator>> fieldOperators = new HashMap<>();
    private final Map<String, CustomFilterWrapper<T>> customFieldFilters = new HashMap<>();

    public FilterContext<T> addFilter(
            String fieldName,
            Operator... operators
    ) {
        if (operators == null || operators.length == 0) {
            throw new IllegalArgumentException("At least one operator must be provided");
        }

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
        return Collections.unmodifiableMap(fieldOperators);
    }

    public Map<String, CustomFilterWrapper<T>> getCustomFieldFilters() {
        return Collections.unmodifiableMap(customFieldFilters);
    }

}
