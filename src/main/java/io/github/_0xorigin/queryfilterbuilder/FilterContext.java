package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.function.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.CustomFilterWrapper;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;

public final class FilterContext<T> {

    private final Map<String, Set<Operator>> fieldOperators;
    private final Map<String, CustomFilterWrapper<T, ?>> customFieldFilters;

    private FilterContext(Builder<T> builder) {
        this.fieldOperators = Map.copyOf(builder.fieldOperators);
        this.customFieldFilters = Map.copyOf(builder.customFieldFilters);
    }

    public static <T> Builder<T> buildForType(Class<T> type) {
        Objects.requireNonNull(type, "Type must not be null");
        return builder();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public Map<String, Set<Operator>> getFieldOperators() {
        return fieldOperators;
    }

    public Map<String, CustomFilterWrapper<T, ?>> getCustomFieldFilters() {
        return customFieldFilters;
    }

    public static class Builder<T> {
        private final Map<String, Set<Operator>> fieldOperators = new HashMap<>();
        private final Map<String, CustomFilterWrapper<T, ?>> customFieldFilters = new HashMap<>();

        private Builder() {}

        public Builder<T> addFilter(
            @NonNull String fieldName,
            @NonNull Operator... operators
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(operators, "Operators must not be null");
            operators = Arrays.stream(operators).filter(Objects::nonNull).toArray(Operator[]::new);
            if (operators.length == 0)
                return this;
            fieldOperators.computeIfAbsent(fieldName, k -> new HashSet<>()).addAll(Arrays.asList(operators));
            return this;
        }

        public <K extends Comparable<? super K> & Serializable> Builder<T> addFilter(
            @NonNull String fieldName,
            @NonNull Class<K> dataType,
            @NonNull CustomFilterFunction<T> filterFunction
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(dataType, "Data type must not be null");
            Objects.requireNonNull(filterFunction, "Filter function must not be null");
            customFieldFilters.put(fieldName, new CustomFilterWrapper<>(dataType, filterFunction));
            return this;
        }

        public FilterContext<T> build() {
            return new FilterContext<>(this);
        }
    }
}
