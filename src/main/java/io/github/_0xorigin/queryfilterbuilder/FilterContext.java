package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.FilterRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomFilterFunction;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.FilterHolder;
import jakarta.persistence.Entity;
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

    private FilterContext(@NonNull final SourceBuilder<T> sourceBuilder) {
        this.filters = Map.copyOf(sourceBuilder.getTemplate().getFilters());
        this.customFilters = Map.copyOf(sourceBuilder.getTemplate().getCustomFilters());
        this.request = sourceBuilder.getRequest().orElse(null);
        this.filterRequests = sourceBuilder.getFilterRequests().orElse(null);
    }

    public static <T> TemplateBuilder<T> buildTemplateForType(@NonNull final Class<T> type) {
        Objects.requireNonNull(type, "Type must not be null");
        if (!type.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Class " + type.getName() + " is not a JPA Entity");
        }
        return templateBuilder();
    }

    private static <T> TemplateBuilder<T> templateBuilder() {
        return new TemplateBuilder<>();
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

    public static final class SourceBuilder<T> {
        private final Template<T> template;
        private HttpServletRequest request;
        private List<FilterRequest> filterRequests;

        private SourceBuilder(@NonNull final Template<T> template) {
            this.template = template;
        }

        public SourceBuilder<T> withQuerySource(@NonNull final HttpServletRequest request) {
            Objects.requireNonNull(request, "HttpServletRequest must not be null");
            this.request = request;
            return this;
        }

        public SourceBuilder<T> withBodySource(@NonNull final List<FilterRequest> filterRequests) {
            Objects.requireNonNull(filterRequests, "FilterRequests must not be null");
            this.filterRequests = filterRequests;
            return this;
        }

        public FilterContext<T> buildFilterContext() {
            return new FilterContext<>(this);
        }

        private Template<T> getTemplate() {
            return template;
        }

        private Optional<HttpServletRequest> getRequest() {
            return Optional.ofNullable(request);
        }

        private Optional<List<FilterRequest>> getFilterRequests() {
            return Optional.ofNullable(filterRequests);
        }
    }

    public static final class Template<T> {
        private final Map<String, FilterHolder<T, ?>> filters;
        private final Map<String, CustomFilterHolder<T, ?>> customFilters;

        private Template(TemplateBuilder<T> templateBuilder) {
            this.filters = Map.copyOf(templateBuilder.getFilters());
            this.customFilters = Map.copyOf(templateBuilder.getCustomFilters());
        }

        private Map<String, FilterHolder<T, ?>> getFilters() {
            return filters;
        }

        private Map<String, CustomFilterHolder<T, ?>> getCustomFilters() {
            return customFilters;
        }

        public SourceBuilder<T> newSourceBuilder() {
            return new SourceBuilder<>(this);
        }
    }

    public static final class TemplateBuilder<T> {
        private final Map<String, FilterHolder<T, ?>> filters = new HashMap<>();
        private final Map<String, CustomFilterHolder<T, ?>> customFilters = new HashMap<>();

        private TemplateBuilder() {}

        public TemplateBuilder<T> queryParam(@NonNull final Consumer<FilterConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for FilterConfigurer must not be null");
            FilterConfigurer<T> filterConfigurer = new FilterConfigurer<>(this, SourceType.QUERY_PARAM);
            configurer.accept(filterConfigurer);
            return this;
        }

        public TemplateBuilder<T> requestBody(@NonNull final Consumer<FilterConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for FilterConfigurer must not be null");
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

        public Template<T> buildTemplate() {
            return new Template<>(this);
        }
    }

    public static class FilterConfigurer<T> {
        private final TemplateBuilder<T> templateBuilder;
        private final SourceType sourceType;

        private FilterConfigurer(@NonNull TemplateBuilder<T> templateBuilder, @NonNull SourceType sourceType) {
            this.templateBuilder = templateBuilder;
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
                throw new IllegalArgumentException("Operators must not be empty");

            var filterHolder = templateBuilder.getFilters()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        existingHolder :
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
                throw new IllegalArgumentException("Operators must not be empty");

            var filterHolder = templateBuilder.getFilters()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new FilterHolder<>(EnumSet.copyOf(existingHolder.operators()), EnumSet.copyOf(existingHolder.sourceTypes()), Optional.of(expressionProviderFunction)) :
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

            var customFilterHolder = templateBuilder.getCustomFilters()
                .compute(filterName, (key, existingHolder) -> {
                    if (existingHolder == null)
                        return new CustomFilterHolder<>(dataTypeForInput, filterFunction, EnumSet.noneOf(SourceType.class));

                    if (!existingHolder.dataType().equals(dataTypeForInput))
                        throw new IllegalArgumentException("Changing data type or filter function for existing custom filter is not allowed: " + filterName);

                    return existingHolder;
                });
            customFilterHolder.sourceTypes().add(sourceType);
            return this;
        }
    }
}
