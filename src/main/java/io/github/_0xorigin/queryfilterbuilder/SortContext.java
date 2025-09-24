package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomSortFunction;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomSortHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.SortHolder;
import jakarta.persistence.Entity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * The {@code SortContext} class encapsulates all the necessary information for building a sort specification.
 * It holds the defined sorts, custom sorts, and the source of the sort requests (either an {@code HttpServletRequest} or a list of {@code SortRequest}).
 *
 * @param <T> The type of the entity to which the sort will be applied.
 */
public final class SortContext<T> {
    private final Map<String, SortHolder<T, ?>> sorts;
    private final Map<String, CustomSortHolder<T>> customSorts;
    private final HttpServletRequest request;
    private final List<SortRequest> sortRequests;

    /**
     * Creates a new {@link SortContext} instance from the given {@link SourceBuilder}.
     *
     * @param sourceBuilder The source builder.
     */
    private SortContext(@NonNull final SourceBuilder<T> sourceBuilder) {
        this.sorts = Map.copyOf(sourceBuilder.getTemplate().getSorts());
        this.customSorts = Map.copyOf(sourceBuilder.getTemplate().getCustomSorts());
        this.request = sourceBuilder.getRequest().orElse(null);
        this.sortRequests = sourceBuilder.getSortRequests().orElse(null);
    }

    /**
     * Creates a {@link TemplateBuilder} for the given entity type.
     * This is the entry point for defining a sort template.
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
     * Returns the map of defined sorts.
     *
     * @return An unmodifiable map of sort holders, keyed by field name.
     */
    public Map<String, SortHolder<T, ?>> getSorts() {
        return sorts;
    }

    /**
     * Returns the map of defined custom sorts.
     *
     * @return An unmodifiable map of custom sort holders, keyed by sort name.
     */
    public Map<String, CustomSortHolder<T>> getCustomSorts() {
        return customSorts;
    }

    /**
     * Returns the {@link HttpServletRequest} if the sort source is query parameters.
     *
     * @return An {@link Optional} containing the request, or empty if the source is not query parameters.
     */
    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    /**
     * Returns the list of {@link SortRequest} if the sort source is the request body.
     *
     * @return An {@link Optional} containing the list of sort requests, or empty if the source is not the request body.
     */
    public Optional<List<SortRequest>> getSortRequests() {
        return Optional.ofNullable(sortRequests);
    }

    /**
     * A builder for specifying the source of the sort data (e.g., query parameters or request body).
     *
     * @param <T> The type of the entity.
     */
    public static final class SourceBuilder<T> {
        private final Template<T> template;
        private HttpServletRequest request;
        private List<SortRequest> sortRequests;

        /**
         * Creates a new {@link SourceBuilder} instance.
         *
         * @param template The template.
         */
        private SourceBuilder(@NonNull final Template<T> template) {
            this.template = template;
        }

        /**
         * Specifies that the sort data will be sourced from an {@link HttpServletRequest} (i.e., query parameters).
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
         * Specifies that the sort data will be sourced from a list of {@link SortRequest} objects (i.e., request body).
         *
         * @param sortRequests The list of sort requests. Must not be null.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the sort requests are null.
         */
        public SourceBuilder<T> withBodySource(@NonNull final List<SortRequest> sortRequests) {
            Objects.requireNonNull(sortRequests, "SortRequests must not be null");
            this.sortRequests = sortRequests;
            return this;
        }

        /**
         * Builds the final {@link SortContext} instance.
         *
         * @return A new {@link SortContext}.
         */
        public SortContext<T> buildSortContext() {
            return new SortContext<>(this);
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
         * Returns the sort requests.
         *
         * @return An {@link Optional} containing the list of sort requests, or empty if not set.
         */
        private Optional<List<SortRequest>> getSortRequests() {
            return Optional.ofNullable(sortRequests);
        }
    }

    /**
     * A template that holds the configuration of all possible sorts for a given entity type.
     *
     * @param <T> The type of the entity.
     */
    public static final class Template<T> {
        private final Map<String, SortHolder<T, ?>> sorts;
        private final Map<String, CustomSortHolder<T>> customSorts;

        /**
         * Creates a new {@link Template} instance.
         *
         * @param templateBuilder The template builder.
         */
        private Template(TemplateBuilder<T> templateBuilder) {
            this.sorts = Map.copyOf(templateBuilder.getSorts());
            this.customSorts = Map.copyOf(templateBuilder.getCustomSorts());
        }

        /**
         * Returns the map of defined sorts.
         *
         * @return An unmodifiable map of sort holders, keyed by field name.
         */
        private Map<String, SortHolder<T, ?>> getSorts() {
            return sorts;
        }

        /**
         * Returns the map of defined custom sorts.
         *
         * @return An unmodifiable map of custom sort holders, keyed by sort name.
         */
        private Map<String, CustomSortHolder<T>> getCustomSorts() {
            return customSorts;
        }

        /**
         * Creates a new {@link SourceBuilder} from this template, which can then be used to specify the source of the sort data.
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
        private final Map<String, SortHolder<T, ?>> sorts = new HashMap<>();
        private final Map<String, CustomSortHolder<T>> customSorts = new HashMap<>();

        /**
         * Creates a new {@link TemplateBuilder} instance.
         */
        private TemplateBuilder() {}

        /**
         * Configures sorts that are sourced from query parameters.
         *
         * @param configurer A consumer that provides a {@link SortConfigurer} to define the sorts.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the configurer is null.
         */
        public TemplateBuilder<T> queryParam(@NonNull final Consumer<SortConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for SortConfigurer must not be null");
            SortConfigurer<T> sortConfigurer = new SortConfigurer<>(this, SourceType.QUERY_PARAM);
            configurer.accept(sortConfigurer);
            return this;
        }

        /**
         * Configures sorts that are sourced from the request body.
         *
         * @param configurer A consumer that provides a {@link SortConfigurer} to define the sorts.
         * @return This builder instance for chaining.
         * @throws NullPointerException if the configurer is null.
         */
        public TemplateBuilder<T> requestBody(@NonNull final Consumer<SortConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for SortConfigurer must not be null");
            SortConfigurer<T> sortConfigurer = new SortConfigurer<>(this, SourceType.REQUEST_BODY);
            configurer.accept(sortConfigurer);
            return this;
        }

        /**
         * Returns the map of defined sorts.
         *
         * @return A map of sort holders, keyed by field name.
         */
        private Map<String, SortHolder<T, ?>> getSorts() {
            return sorts;
        }

        /**
         * Returns the map of defined custom sorts.
         *
         * @return A map of custom sort holders, keyed by sort name.
         */
        private Map<String, CustomSortHolder<T>> getCustomSorts() {
            return customSorts;
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
     * A configurer for defining sorts for a specific source type (query parameter or request body).
     * <p>
     * When a sort is defined multiple times for the same field, the directions and source types are merged.
     * If an {@code ExpressionProviderFunction} is specified, it will overwrite any existing provider for that field.
     *
     * @param <T> The type of the entity.
     */
    public static class SortConfigurer<T> {
        private final TemplateBuilder<T> templateBuilder;
        private final SourceType sourceType;

        /**
         * Creates a new {@link SortConfigurer} instance.
         *
         * @param templateBuilder The template builder.
         * @param sourceType      The source type.
         */
        private SortConfigurer(@NonNull TemplateBuilder<T> templateBuilder, @NonNull SourceType sourceType) {
            this.templateBuilder = templateBuilder;
            this.sourceType = sourceType;
        }

        /**
         * Adds an ascending sort for the specified field.
         * <p>
         * If a sort with the same {@code fieldName} already exists, the existing sort is used as-is
         * and the new direction is added to it. If no sort exists, a new one is created.
         *
         * @param fieldName The name of the field to sort by. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException if fieldName is null.
         */
        public SortConfigurer<T> addAscSort(@NonNull final String fieldName) {
            return addSort(fieldName, Sort.Direction.ASC);
        }

        /**
         * Adds a descending sort for the specified field.
         * <p>
         * If a sort with the same {@code fieldName} already exists, the existing sort is used as-is
         * and the new direction is added to it. If no sort exists, a new one is created.
         *
         * @param fieldName The name of the field to sort by. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException if fieldName is null.
         */
        public SortConfigurer<T> addDescSort(@NonNull final String fieldName) {
            return addSort(fieldName, Sort.Direction.DESC);
        }

        /**
         * Adds both ascending and descending sort options for the specified field.
         * <p>
         * If a sort with the same {@code fieldName} already exists, the existing sort is used as-is
         * and the new directions are added to it. If no sort exists, a new one is created.
         *
         * @param fieldName The name of the field to sort by. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException if fieldName is null.
         */
        public SortConfigurer<T> addSorts(@NonNull final String fieldName) {
            return addSort(fieldName, Sort.Direction.ASC, Sort.Direction.DESC);
        }

        /**
         * Adds an ascending sort with a custom expression provider.
         * This is useful when the sort logic requires more than a simple field comparison.
         * <p>
         * If a sort with the same {@code fieldName} already exists, this new definition will overwrite the existing
         * expression provider. The directions and source types will be merged.
         *
         * @param fieldName                The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param <K>                      The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addAscSort(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.ASC);
        }

        /**
         * Adds a descending sort with a custom expression provider.
         * This is useful when the sort logic requires more than a simple field comparison.
         * <p>
         * If a sort with the same {@code fieldName} already exists, this new definition will overwrite the existing
         * expression provider. The directions and source types will be merged.
         *
         * @param fieldName                The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param <K>                      The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addDescSort(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.DESC);
        }

        /**
         * Adds both ascending and descending sort options with a custom expression provider.
         * This is useful when the sort logic requires more than a simple field comparison.
         * <p>
         * If a sort with the same {@code fieldName} already exists, this new definition will overwrite the existing
         * expression provider. The directions and source types will be merged.
         *
         * @param fieldName                The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param <K>                      The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSorts(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.ASC, Sort.Direction.DESC);
        }

        /**
         * Adds a custom sort with a specific name and sort function.
         * Custom sorts allow for complex sorting logic that cannot be expressed with standard field-based sorting.
         * <p>
         * If a custom sort with the same name already exists, the source type is merged with the existing sort.
         * The sort function will only be set if the custom sort doesn't already exist.
         *
         * @param sortName     The name of the custom sort. Must not be null.
         * @param sortFunction The function that implements the custom sort logic. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException if any of the arguments are null.
         */
        public SortConfigurer<T> addCustomSort(
            @NonNull final String sortName,
            @NonNull final CustomSortFunction<T> sortFunction
        ) {
            Objects.requireNonNull(sortName, "Sort name must not be null");
            Objects.requireNonNull(sortFunction, "Sort function must not be null");

            var customSortHolder = templateBuilder.getCustomSorts()
                .compute(sortName, (key, existingHolder) -> (
                    existingHolder != null ?
                        existingHolder :
                        new CustomSortHolder<>(sortFunction, EnumSet.noneOf(SourceType.class))
                ));
            customSortHolder.sourceTypes().add(sourceType);
            return this;
        }

        /**
         * Adds a sort for the specified field.
         * <p>
         * If a sort with the same {@code fieldName} already exists, the existing sort is used as-is
         * and the new directions are added to it. If no sort exists, a new one is created.
         *
         * @param fieldName The name of the field to sort by. Must not be null.
         * @param directions The sort directions. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if fieldName or directions are null.
         */
        private SortConfigurer<T> addSort(
            @NonNull final String fieldName,
            @NonNull Sort.Direction... directions
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(directions, "Directions must not be null");

            var sortHolder = templateBuilder.getSorts()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        existingHolder :
                        new SortHolder<>(EnumSet.noneOf(Sort.Direction.class), EnumSet.noneOf(SourceType.class), Optional.empty())
                ));
            sortHolder.directions().addAll(Arrays.stream(directions).toList());
            sortHolder.sourceTypes().add(sourceType);
            return this;
        }

        /**
         * Adds a sort with a custom expression provider.
         * <p>
         * If a sort with the same {@code fieldName} already exists, this new definition will overwrite the existing
         * expression provider. The directions and source types will be merged.
         *
         * @param fieldName                The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param directions               The sort directions. Must not be null.
         * @param <K>                      The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         */
        private <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSort(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
            @NonNull Sort.Direction... directions
        ) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            Objects.requireNonNull(expressionProviderFunction, "Expression provider function must not be null");
            Objects.requireNonNull(directions, "Directions must not be null");

            var sortHolder = templateBuilder.getSorts()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new SortHolder<>(EnumSet.copyOf(existingHolder.directions()), EnumSet.copyOf(existingHolder.sourceTypes()), Optional.of(expressionProviderFunction)) :
                        new SortHolder<>(EnumSet.noneOf(Sort.Direction.class), EnumSet.noneOf(SourceType.class), Optional.of(expressionProviderFunction))
                ));
            sortHolder.directions().addAll(Arrays.stream(directions).toList());
            sortHolder.sourceTypes().add(sourceType);
            return this;
        }
    }
}
