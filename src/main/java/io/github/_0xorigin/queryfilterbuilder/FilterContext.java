package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.function.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.CustomFilterWrapper;

import java.io.Serializable;
import java.util.*;

public final class FilterContext<T> {

    private final Map<String, Set<Operator>> fieldOperators = new HashMap<>();
    private final Map<String, CustomFilterWrapper<T, ?>> customFieldFilters = new HashMap<>();

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

    public <K extends Comparable<? super K> & Serializable> FilterContext<T> addFilter(
        String fieldName,
        Class<K> dataType,
        CustomFilterFunction<T> filterFunction
    ) {
        customFieldFilters.put(fieldName, new CustomFilterWrapper<>(dataType, filterFunction));
        return this;
    }

    public Map<String, Set<Operator>> getFieldOperators() {
        return Collections.unmodifiableMap(fieldOperators);
    }

    public Map<String, CustomFilterWrapper<T, ?>> getCustomFieldFilters() {
        return Collections.unmodifiableMap(customFieldFilters);
    }
}
