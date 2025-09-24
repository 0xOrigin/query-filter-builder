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

/**
 * The {@code FilterContext} class encapsulates all the necessary information for building a filter specification.
 * It holds the defined filters, custom filters, and the source of the filter requests (either an {@code HttpServletRequest} or a list of {@code FilterRequest}).
 *
 * @param <T> The type of the entity to which the filter will be applied.
 */
public final class FilterContext<T> {
    private final Map<String, FilterHolder<T, ?>> filters;
    private final Map<String, CustomFilterHolder<T, ?>> customFilters;
    private final HttpServletRequest request;
    private final List<FilterRequest> filterRequests;

    /**
     * Creates a new {@link FilterContext} instance from the given {@link SourceBuilder}.
     *
     * @param sourceBuilder The source builder.
     */
    private FilterContext(@NonNull final SourceBuilder<T> sourceBuilder) {
        this.filters = Map.copyOf(sourceBuilder.getTemplate().getFilters());
        this.customFilters = Map.copyOf(sourceBuilder.getTemplate().getCustomFilters());
        this.request = sourceBuilder.getRequest().orElse(null);
        this.filterRequests = sourceBuilder.getFilterRequests().orElse(null);
    }

    /**
     * Creates a {@link TemplateBuilder} for the given entity type.
     * This is the entry point for defining a filter template.
     *
     * @param type The entity class. Must not be null and must be annotated with {@link Entity}.
     * @param <T>  The type of the entity.
     * @return A new {@link TemplateBuilder} instance.
     * @throws NullPointerException     if the type is null.
     * @throws IllegalArgumentException if the class is not a JPA Entity.
     */
    public static <T> TemplateBuilder<T> buildTemplateForType(@NonNull final Class<T> type) {
        Objects.requireNonNull(type, "Type must not be null");
        if (!type.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("Class " + type.getName() + " is not a JPA Entity");
        }
        return templateBuilder();
    }

    /**
     * Returns a new {@link TemplateBuilder} instance.
     *
     * @param <T> The type of the entity.
     * @return A new {@link TemplateBuilder} instance.
     */
    private static <T> TemplateBuilder<T> templateBuilder() {
        return new TemplateBuilder<>();
    }

    /**
     * Returns the map of defined filters.
     *
     * @return An unmodifiable map of filter holders, keyed by field name.
     */
    public Map<String, FilterHolder<T, ?>> getFilters() {
        return filters;
    }

    /**
     * Returns the map of defined custom filters.
     *
     * @return An unmodifiable map of custom filter holders, keyed by filter name.
     */
    public Map<String, CustomFilterHolder<T, ?>> getCustomFilters() {
        return customFilters;
    }

    /**
     * Returns the {@link HttpServletRequest} if the filter source is query parameters.
     *
     * @return An {@link Optional} containing the request, or empty if the source is not query parameters.
     */
    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * Returns the list of {@link FilterRequest} if the filter source is the request body.
     *
     * @return An {@link Optional} containing the list of filter requests, or empty if the source is not the request body.
     */
    public Optional<List<FilterRequest>> getFilterRequests() {
        return Optional.ofNullable(filterRequests);
    }

    /**
     * A builder for specifying the source of the filter data (e.g., query parameters or request body).
     *
     * @param <T> The type of the entity.
     */
    public static final class SourceBuilder<T> {
        private final Template<T> template;
        private HttpServletRequest request;
        private List<FilterRequest> filterRequests;

        /**
         * Creates a new {@link SourceBuilder} instance.
         *
         * @param template The template.
         */
        private SourceBuilder(@NonNull final Template<T> template) {
            this.template = template;
        }

        /**
         * Specifies that the filter data will be sourced from an {@link HttpServletRequest} (i.e., query parameters).
         *
         * @param request The HTTP request. Must not be null.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the request is null.
         */
        public SourceBuilder<T> withQuerySource(@NonNull final HttpServletRequest request) {
            Objects.requireNonNull(request, "HttpServletRequest must not be null");
            this.request = request;
            return this;
        }

        /**
         * Specifies that the filter data will be sourced from a list of {@link FilterRequest} objects (i.e., request body).
         *
         * @param filterRequests The list of filter requests. Must not be null.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the filter requests are null.
         */
        public SourceBuilder<T> withBodySource(@NonNull final List<FilterRequest> filterRequests) {
            Objects.requireNonNull(filterRequests, "FilterRequests must not be null");
            this.filterRequests = filterRequests;
            return this;
        }

        /**
         * Builds the final {@link FilterContext} instance.
         *
         * @return A new {@link FilterContext}.
         */
        public FilterContext<T> buildFilterContext() {
            return new FilterContext<>(this);
        }

        /**
         * Returns the template.
         *
         * @return The template.
         */
        private Template<T> getTemplate() {
            return template;
        }

        /**
         * Returns the request.
         *
         * @return An {@link Optional} containing the request, or empty if not set.
         */
        private Optional<HttpServletRequest> getRequest() {
            return Optional.ofNullable(request);
        }

        /**
         * Returns the filter requests.
         *
         * @return An {@link Optional} containing the filter requests, or empty if not set.
         */
        private Optional<List<FilterRequest>> getFilterRequests() {
            return Optional.ofNullable(filterRequests);
        }
    }

    /**
     * A template that holds the configuration of all possible filters for a given entity type.
     *
     * @param <T> The type of the entity.
     */
    public static final class Template<T> {
        private final Map<String, FilterHolder<T, ?>> filters;
        private final Map<String, CustomFilterHolder<T, ?>> customFilters;

        /**
         * Creates a new {@link Template} instance.
         *
         * @param templateBuilder The template builder.
         */
        private Template(TemplateBuilder<T> templateBuilder) {
            this.filters = Map.copyOf(templateBuilder.getFilters());
            this.customFilters = Map.copyOf(templateBuilder.getCustomFilters());
        }

        /**
         * Returns the map of defined filters.
         *
         * @return An unmodifiable map of filter holders, keyed by field name.
         */
        private Map<String, FilterHolder<T, ?>> getFilters() {
            return filters;
        }

        /**
         * Returns the map of defined custom filters.
         *
         * @return An unmodifiable map of custom filter holders, keyed by filter name.
         */
        private Map<String, CustomFilterHolder<T, ?>> getCustomFilters() {
            return customFilters;
        }

        /**
         * Creates a new {@link SourceBuilder} from this template, which can then be used to specify the source of the filter data.
         *
         * @return A new {@link SourceBuilder}.
         */
        public SourceBuilder<T> newSourceBuilder() {
            return new SourceBuilder<>(this);
        }
    }

    /**
     * A builder for creating a {@link Template}.
     *
     * @param <T> The type of the entity.
     */
    public static final class TemplateBuilder<T> {
        private final Map<String, FilterHolder<T, ?>> filters = new HashMap<>();
        private final Map<String, CustomFilterHolder<T, ?>> customFilters = new HashMap<>();

        /**
         * Creates a new {@link TemplateBuilder} instance.
         */
        private TemplateBuilder() {}

        /**
         * Configures filters that are sourced from query parameters.
         *
         * @param configurer A consumer that provides a {@link FilterConfigurer} to define the filters.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the configurer is null.
         */
        public TemplateBuilder<T> queryParam(@NonNull final Consumer<FilterConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for FilterConfigurer must not be null");
            FilterConfigurer<T> filterConfigurer = new FilterConfigurer<>(this, SourceType.QUERY_PARAM);
            configurer.accept(filterConfigurer);
            return this;
        }

        /**
         * Configures filters that are sourced from the request body.
         *
         * @param configurer A consumer that provides a {@link FilterConfigurer} to define the filters.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the configurer is null.
         */
        public TemplateBuilder<T> requestBody(@NonNull final Consumer<FilterConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for FilterConfigurer must not be null");
            FilterConfigurer<T> filterConfigurer = new FilterConfigurer<>(this, SourceType.REQUEST_BODY);
            configurer.accept(filterConfigurer);
            return this;
        }

        /**
         * Returns the map of defined filters.
         *
         * @return A map of filter holders, keyed by field name.
         */
        private Map<String, FilterHolder<T, ?>> getFilters() {
            return filters;
        }

        /**
         * Returns the map of defined custom filters.
         *
         * @return A map of custom filter holders, keyed by filter name.
         */
        private Map<String, CustomFilterHolder<T, ?>> getCustomFilters() {
            return customFilters;
        }

        /**
         * Builds the final {@link Template} instance.
         *
         * @return A new {@link Template}.
         */
        public Template<T> buildTemplate() {
            return new Template<>(this);
        }
    }

    /**
     * A configurer for defining filters for a specific source type (query parameter or request body).
     * <p>
     * When a filter is defined multiple times for the same field, the operators and source types are merged.
     * If an {@code ExpressionProviderFunction} is specified, it will overwrite any existing provider for that field.
     *
     * @param <T> The type of the entity.
     */
    public static class FilterConfigurer<T> {
        private final TemplateBuilder<T> templateBuilder;
        private final SourceType sourceType;

        /**
         * Creates a new {@link FilterConfigurer} instance.
         *
         * @param templateBuilder The template builder.
         * @param sourceType      The source type.
         */
        private FilterConfigurer(@NonNull TemplateBuilder<T> templateBuilder, @NonNull SourceType sourceType) {
            this.templateBuilder = templateBuilder;
            this.sourceType = sourceType;
        }

        /**
         * Adds a filter for a specific field with the given operators.
         * <p>
         * If a filter with the same {@code fieldName} already exists, the existing filter is used as-is
         * and the new operators are added to it. If no filter exists, a new one is created.
         *
         * @param fieldName The name of the field to filter on.
         * @param operators The allowed operators for this filter.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if fieldName or operators are null.
         * @throws IllegalArgumentException if no operators are provided.
         */
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

        /**
         * Adds a filter with a custom expression provider.
         * This is useful when the filter logic requires more than a simple field comparison.
         * <p>
         * If a filter with the same {@code fieldName} already exists, this new definition will overwrite the existing
         * expression provider. The operators and source types will be merged.
         *
         * @param fieldName                The name of the filter.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}.
         * @param operators                The allowed operators for this filter.
         * @param <K>                      The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if no operators are provided.
         */
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

        /**
         * Adds a custom filter with a specific name, data type, and filter function.
         * Custom filters allow for complex filtering logic that cannot be expressed with standard operators.
         * <p>
         * If a custom filter with the same name is added again, the source type is merged. However, changing the
         * data type or the filter function for an existing custom filter is not allowed and will result in an
         * {@link IllegalArgumentException}.
         *
         * @param filterName     The name of the custom filter.
         * @param dataTypeForInput The expected data type of the input value for this filter.
         * @param filterFunction The function that implements the custom filter logic.
         * @param <K>            The type of the input value.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if a custom filter with the same name but a different data type is already defined.
         */
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
