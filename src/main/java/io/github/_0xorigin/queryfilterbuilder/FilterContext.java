package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dto.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.function.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.CustomFilterWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class FilterContext<T> {
    private final Map<String, Set<Operator>> fieldOperators;
    private final Map<String, CustomFilterWrapper<T, ?>> customFieldFilters;
    private final Map<String, Set<SourceType>> fieldSourceTypes;
    private final HttpServletRequest request;
    private final List<FilterRequest> filterRequests;

    private FilterContext(@NonNull final Builder<T> builder) {
        this.fieldOperators = Map.copyOf(builder.getFieldOperators());
        this.customFieldFilters = Map.copyOf(builder.getCustomFieldFilters());
        this.fieldSourceTypes = Map.copyOf(builder.getFieldSourceTypes());
        this.request = builder.getRequest().orElse(null);
        this.filterRequests = builder.getFilterRequests().orElse(null);
    }

    public static <T> Builder<T> buildForType(@NonNull Class<T> type) {
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

    public Map<String, Set<SourceType>> getFieldSourceTypes() {
        return fieldSourceTypes;
    }

    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public Optional<List<FilterRequest>> getFilterRequests() {
        return Optional.ofNullable(filterRequests);
    }

    public static final class Builder<T> {
        private final Map<String, Set<Operator>> fieldOperators = new HashMap<>();
        private final Map<String, CustomFilterWrapper<T, ?>> customFieldFilters = new HashMap<>();
        private final Map<String, Set<SourceType>> fieldSourceTypes = new HashMap<>();
        private HttpServletRequest request;
        private List<FilterRequest> filterRequests;

        private Builder() {}

        public Builder<T> queryParam(
            @NonNull final HttpServletRequest request,
            @NonNull final Consumer<FilterConfigurer<T>> configurer
        ) {
            Objects.requireNonNull(request, "HttpServletRequest must not be null");
            Objects.requireNonNull(configurer, "Consumer for FilterConfigurer must not be null");
            this.request = Optional.ofNullable(this.request).orElse(request);
            FilterConfigurer<T> filterConfigurer = new FilterConfigurer<>(this, SourceType.QUERY_PARAM);
            configurer.accept(filterConfigurer);
            return this;
        }

        public Builder<T> requestBody(
            @NonNull final List<FilterRequest> filters,
            @NonNull final Consumer<FilterConfigurer<T>> configurer
        ) {
            Objects.requireNonNull(filters, "FilterRequest list must not be null");
            Objects.requireNonNull(configurer, "Consumer for FilterConfigurer must not be null");
            this.filterRequests = Optional.ofNullable(this.filterRequests).orElse(filters);
            FilterConfigurer<T> filterConfigurer = new FilterConfigurer<>(this, SourceType.REQUEST_BODY);
            configurer.accept(filterConfigurer);
            return this;
        }

        private Map<String, Set<Operator>> getFieldOperators() {
            return fieldOperators;
        }

        private Map<String, CustomFilterWrapper<T, ?>> getCustomFieldFilters() {
            return customFieldFilters;
        }

        private Map<String, Set<SourceType>> getFieldSourceTypes() {
            return fieldSourceTypes;
        }

        private Optional<HttpServletRequest> getRequest() {
            return Optional.ofNullable(request);
        }

        private Optional<List<FilterRequest>> getFilterRequests() {
            return Optional.ofNullable(filterRequests);
        }

        public FilterContext<T> build() {
            return new FilterContext<>(this);
        }
    }

    public static class FilterConfigurer<T> {
        private final Builder<T> builder;
        private final SourceType sourceType;

        private FilterConfigurer(@NonNull Builder<T> builder, @NonNull SourceType sourceType) {
            this.builder = builder;
            this.sourceType = sourceType;
        }

        public FilterConfigurer<T> addFilter(
            @NonNull final String fieldName,
            @NonNull Operator... operators
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(operators, "Operators must not be null");
            EnumSet<Operator> operatorsSet = Arrays.stream(operators)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Operator.class)));
            if (operatorsSet.isEmpty())
                return this;
            builder.getFieldOperators().computeIfAbsent(fieldName, k -> EnumSet.noneOf(Operator.class)).addAll(operatorsSet);
            builder.getFieldSourceTypes().computeIfAbsent(fieldName, k -> EnumSet.noneOf(SourceType.class)).add(sourceType);
            return this;
        }

        public <K extends Comparable<? super K> & Serializable> FilterConfigurer<T> addFilter(
            @NonNull final String fieldName,
            @NonNull final Class<K> dataType,
            @NonNull final CustomFilterFunction<T> filterFunction
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(dataType, "Data type must not be null");
            Objects.requireNonNull(filterFunction, "Filter function must not be null");
            builder.getCustomFieldFilters().put(fieldName, new CustomFilterWrapper<>(dataType, filterFunction));
            builder.getFieldSourceTypes().computeIfAbsent(fieldName, k -> EnumSet.noneOf(SourceType.class)).add(sourceType);
            return this;
        }
    }
}
