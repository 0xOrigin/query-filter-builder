package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.FilterHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class FilterContext<T> {
    private final Map<String, FilterHolder<T, ?>> filters;
    private final Map<String, CustomFilterHolder<T, ?>> customFilters;
    private final HttpServletRequest request;
    private final List<FilterRequest> filterRequests;

    private FilterContext(@NonNull final Builder<T> builder) {
        this.filters = Map.copyOf(builder.getFilters());
        this.customFilters = Map.copyOf(builder.getCustomFilters());
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

    public Map<String, FilterHolder<T, ?>> getFilters() {
        return filters;
    }

    public Map<String, CustomFilterHolder<T, ?>> getCustomFilters() {
        return customFilters;
    }

    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public Optional<List<FilterRequest>> getFilterRequests() {
        return Optional.ofNullable(filterRequests);
    }

    public static final class Builder<T> {
        private final Map<String, FilterHolder<T, ?>> filters = new HashMap<>();
        private final Map<String, CustomFilterHolder<T, ?>> customFilters = new HashMap<>();
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

        private Map<String, FilterHolder<T, ?>> getFilters() {
            return filters;
        }

        private Map<String, CustomFilterHolder<T, ?>> getCustomFilters() {
            return customFilters;
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

            var filterHolder = builder.getFilters()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new FilterHolder<>(EnumSet.copyOf(existingHolder.operators()), EnumSet.copyOf(existingHolder.sourceTypes()), existingHolder.expressionProviderFunction()) :
                        new FilterHolder<>(EnumSet.noneOf(Operator.class), EnumSet.noneOf(SourceType.class), Optional.empty())
                ));
            filterHolder.operators().addAll(operatorsSet);
            filterHolder.sourceTypes().add(sourceType);
            return this;
        }

        public <K extends Comparable<? super K> & Serializable> FilterConfigurer<T> addFilter(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
            @NonNull Operator... operators
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(expressionProviderFunction, "Expression provider function must not be null");
            Objects.requireNonNull(operators, "Operators must not be null");
            EnumSet<Operator> operatorsSet = Arrays.stream(operators)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Operator.class)));
            if (operatorsSet.isEmpty())
                return this;

            var filterHolder = builder.getFilters()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new FilterHolder<>(EnumSet.copyOf(existingHolder.operators()), EnumSet.copyOf(existingHolder.sourceTypes()), existingHolder.expressionProviderFunction()) :
                        new FilterHolder<>(EnumSet.noneOf(Operator.class), EnumSet.noneOf(SourceType.class), Optional.of(expressionProviderFunction))
                ));
            filterHolder.operators().addAll(operatorsSet);
            filterHolder.sourceTypes().add(sourceType);
            return this;
        }

        public <K extends Comparable<? super K> & Serializable> FilterConfigurer<T> addCustomFilter(
            @NonNull final String filterName,
            @NonNull final Class<K> dataTypeForInput,
            @NonNull final CustomFilterFunction<T> filterFunction
        ) {
            Objects.requireNonNull(filterName, "Filter name must not be null");
            Objects.requireNonNull(dataTypeForInput, "Data type for input must not be null");
            Objects.requireNonNull(filterFunction, "Filter function must not be null");

            var customFilterHolder = builder.getCustomFilters()
                .compute(filterName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new CustomFilterHolder<>(existingHolder.dataType(), existingHolder.customFilterFunction(), EnumSet.copyOf(existingHolder.sourceTypes())) :
                        new CustomFilterHolder<>(dataTypeForInput, filterFunction, EnumSet.noneOf(SourceType.class))
                ));
            customFilterHolder.sourceTypes().add(sourceType);
            return this;
        }
    }
}
