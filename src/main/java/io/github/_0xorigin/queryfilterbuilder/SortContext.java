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
import java.util.stream.Collectors;

/**
 * The {@code SortContext} class encapsulates all the necessary information for building a sort specification.
 * It holds the defined sorts, custom sorts, and the source of the sort requests (either an {@code HttpServletRequest} or a list of {@code SortRequest}).
 *
 * @param <T> The type of the entity to which the sort will be applied.
 */
public final class SortContext<T> {
    private final Map<String, SortHolder<T, ? extends Comparable<?>>> sorts;
    private final Map<String, CustomSortHolder<T>> customSorts;
    private final Map<String, String> aliasToFieldMap;
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
        this.aliasToFieldMap = Map.copyOf(sourceBuilder.getTemplate().getAliasToFieldMap());
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
    public Map<String, SortHolder<T, ? extends Comparable<?>>> getSorts() {
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
     * Returns the alias-to-entity field mapping for sorts. Keys are aliases (client-facing) and values are the entity field names.
     */
    public Map<String, String> getAliasToFieldMap() {
        return aliasToFieldMap;
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
        private final Map<String, SortHolder<T, ? extends Comparable<?>>> sorts;
        private final Map<String, CustomSortHolder<T>> customSorts;
        private final Map<String, String> aliasToFieldMap;

        /**
         * Creates a new {@link Template} instance.
         *
         * @param templateBuilder The template builder.
         */
        private Template(TemplateBuilder<T> templateBuilder) {
            this.sorts = Map.copyOf(templateBuilder.getSorts());
            this.aliasToFieldMap = Map.copyOf(templateBuilder.getAliasToField());
            this.customSorts = Map.copyOf(templateBuilder.getCustomSorts());
        }

        /**
         * Returns the map of defined sorts.
         *
         * @return An unmodifiable map of sort holders, keyed by field name.
         */
        private Map<String, SortHolder<T, ? extends Comparable<?>>> getSorts() {
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

        private Map<String, String> getAliasToFieldMap() {
            return aliasToFieldMap;
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
        private final Map<String, SortHolder<T, ? extends Comparable<?>>> sorts = new HashMap<>();
        private final Map<String, CustomSortHolder<T>> customSorts = new HashMap<>();
        private final Map<String, String> aliasToField = new HashMap<>();

        /**
         * Creates a new {@link TemplateBuilder} instance.
         */
        private TemplateBuilder() {
        }

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
        private Map<String, SortHolder<T, ? extends Comparable<?>>> getSorts() {
            return sorts;
        }

        /**
         * Returns the map of alias-to-field mappings.
         *
         * @return A map of aliases, keyed by field name.
         */
        private Map<String, String> getAliasToField() {
            return aliasToField;
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
         * Convert the provided directions array into an {@link EnumSet} while filtering out null entries.
         * This helper normalizes the directions input and returns an empty {@code EnumSet} if no valid
         * directions are present.
         *
         * @param directions the directions array passed by the caller; must not be null
         * @return an {@link EnumSet} containing the non-null directions
         */
        private static EnumSet<Sort.Direction> getDirectionsSet(@NonNull Sort.Direction[] directions) {
            return Arrays.stream(directions)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Sort.Direction.class)));
        }

        /**
         * Validate that a field name is neither {@code null} nor blank.
         */
        private static void validateFieldName(String fieldName) {
            Objects.requireNonNull(fieldName, "Field name must not be null");
            if (fieldName.isBlank())
                throw new IllegalArgumentException("Field name must not be blank");
        }

        /**
         * Validate that the directions array is not {@code null}.
         */
        private static void validateDirectionsArray(Sort.Direction[] directions) {
            Objects.requireNonNull(directions, "Directions must not be null");
        }

        /**
         * Validate that the directions set is not empty.
         */
        private static void validateDirectionsSet(EnumSet<Sort.Direction> directionsSet) {
            if (directionsSet.isEmpty())
                throw new IllegalArgumentException("Directions must not be empty");
        }

        /**
         * Validate that the provided expression provider function is not {@code null}.
         *
         * @param expressionProviderFunction the function to validate
         * @param <T>                        entity type parameter used by the function
         * @param <K>                        expression result type used by the function
         * @throws NullPointerException if {@code expressionProviderFunction} is null
         */
        private static <T, K extends Comparable<? super K> & Serializable> void validateExpressionProviderFunction(
                ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            Objects.requireNonNull(expressionProviderFunction, "Expression provider function must not be null");
        }

        /**
         * Validate that the provided custom sort function is not {@code null}.
         *
         * @param sortFunction the custom sort function to validate
         * @param <T>          the entity type parameter for the sort
         * @throws NullPointerException if {@code sortFunction} is null
         */
        private static <T> void validateCustomSortFunction(CustomSortFunction<T> sortFunction) {
            Objects.requireNonNull(sortFunction, "Sort function must not be null");
        }

        /**
         * Validate that a custom sort name is neither {@code null} nor blank.
         *
         * @param sortName the custom sort name to validate
         * @throws NullPointerException     if {@code sortName} is null
         * @throws IllegalArgumentException if {@code sortName} is blank
         */
        private static void validateSortName(String sortName) {
            Objects.requireNonNull(sortName, "Sort name must not be null");
            if (sortName.isBlank())
                throw new IllegalArgumentException("Sort name must not be blank");
        }

        /**
         * Common implementation for adding a simple (field-based) sort.
         * Validates inputs and delegates to {@link #addSortHolder(String, Sort.Direction[])} to update the template.
         *
         * @param fieldName  the entity field name
         * @param directions allowed sort directions
         */
        private void addBaseSort(@NonNull final String fieldName, @NonNull Sort.Direction... directions) {
            validateFieldName(fieldName);
            validateDirectionsArray(directions);
            EnumSet<Sort.Direction> directionsSet = getDirectionsSet(directions);
            validateDirectionsSet(directionsSet);
            addSortHolder(fieldName, directions);
        }

        /**
         * Common implementation for adding a sort that uses a custom expression provider.
         * Validates inputs and delegates to {@link #addSortHolder(String, ExpressionProviderFunction, Sort.Direction[])}.
         *
         * @param fieldName                  the entity field name
         * @param expressionProviderFunction custom expression provider to compute the sort expression
         * @param directions                 allowed sort directions
         * @param <K>                        expression result type
         */
        private <K extends Comparable<? super K> & Serializable> void addBaseSort(
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
                @NonNull Sort.Direction... directions
        ) {
            validateFieldName(fieldName);
            validateExpressionProviderFunction(expressionProviderFunction);
            validateDirectionsArray(directions);
            EnumSet<Sort.Direction> directionsSet = getDirectionsSet(directions);
            validateDirectionsSet(directionsSet);
            addSortHolder(fieldName, expressionProviderFunction, directions);
        }

        /**
         * Validate a set of aliases ensuring the set is not null or empty and that each alias is valid
         * for the provided {@code fieldName}.
         *
         * @param aliases   the set of aliases to validate
         * @param fieldName the entity field name the aliases will map to
         */
        private void validateAliasesSet(Set<String> aliases, String fieldName) {
            Objects.requireNonNull(aliases, "Aliases must not be null");
            if (aliases.isEmpty())
                throw new IllegalArgumentException("Aliases must not be empty");
            aliases.forEach(alias -> validateAlias(alias, fieldName));
        }

        /**
         * Validate a single alias: non-null, non-blank, and not already mapped to a different field.
         *
         * @param alias     the alias to validate
         * @param fieldName the entity field name the alias will map to
         * @throws NullPointerException     if {@code alias} is null
         * @throws IllegalArgumentException if {@code alias} is blank or already mapped to another field
         */
        private void validateAlias(String alias, String fieldName) {
            Objects.requireNonNull(alias, "Alias must not be null");
            if (alias.isBlank())
                throw new IllegalArgumentException("Alias must not be blank");

            String existing = templateBuilder.getAliasToField().get(alias);
            if (existing != null && !existing.equals(fieldName))
                throw new IllegalArgumentException("Alias '" + alias + "' is already mapped to a different field: " + existing);
        }

        /**
         * Adds an ascending sort for the specified field.
         * <p>
         * If a sort with the same {@code fieldName} already exists, the existing sort is used as-is
         * and the new direction is added to it. If no sort exists, a new one is created.
         *
         * @param fieldName The name of the field to sort by. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if fieldName is null.
         * @throws IllegalArgumentException if fieldName is blank.
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
         * @throws NullPointerException     if fieldName is null.
         * @throws IllegalArgumentException if fieldName is blank.
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
         * @throws NullPointerException     if fieldName is null.
         * @throws IllegalArgumentException if fieldName is blank.
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
         * @param fieldName                  The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param <K>                        The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if fieldName is blank.
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
         * @param fieldName                  The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param <K>                        The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if fieldName is blank.
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
         * @param fieldName                  The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param <K>                        The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if fieldName is blank.
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSorts(
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.ASC, Sort.Direction.DESC);
        }

        /**
         * Register a single client-facing alias that maps to the given entity field and
         * allow ascending sorting for that field.
         * <p>
         * The provided {@code alias} is what clients will use when submitting sort parameters;
         * internally it will be mapped to {@code fieldName}.
         *
         * @param alias     client-facing alias to register for this field; must not be null or blank
         * @param fieldName entity field name the alias maps to; must not be null or blank
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if alias is blank or already mapped to a different field, or if fieldName is blank
         */
        public SortConfigurer<T> addAscSort(@NonNull final String alias, @NonNull final String fieldName) {
            addSort(alias, fieldName, Sort.Direction.ASC);
            return this;
        }

        /**
         * Register a single client-facing alias that maps to the given entity field and
         * allow descending sorting for that field.
         *
         * @param alias     client-facing alias to register for this field; must not be null or blank
         * @param fieldName entity field name the alias maps to; must not be null or blank
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if alias is blank or already mapped to a different field, or if fieldName is blank
         */
        public SortConfigurer<T> addDescSort(@NonNull final String alias, @NonNull final String fieldName) {
            addSort(alias, fieldName, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register a single client-facing alias that maps to the given entity field and
         * allow both ascending and descending sorting for that field.
         *
         * @param alias     client-facing alias to register for this field; must not be null or blank
         * @param fieldName entity field name the alias maps to; must not be null or blank
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if alias is blank or already mapped to a different field, or if fieldName is blank
         */
        public SortConfigurer<T> addSorts(@NonNull final String alias, @NonNull final String fieldName) {
            addSort(alias, fieldName, Sort.Direction.ASC, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register a single client-facing alias that maps to the given field and attach an
         * {@link ExpressionProviderFunction} to compute the sort expression. The registered alias
         * will be resolvable by client sort parameters and the provided expression will be used
         * to build the JPA criteria expression when sorting.
         *
         * @param alias                      client-facing alias to register; must not be null or blank
         * @param fieldName                  entity field name the alias maps to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param <K>                        expression result type
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if alias is blank or already mapped to a different field, or if fieldName is blank
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addAscSort(
                @NonNull final String alias,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            addSort(alias, fieldName, expressionProviderFunction, Sort.Direction.ASC);
            return this;
        }

        /**
         * Register a single client-facing alias that maps to the given field and attach an
         * {@link ExpressionProviderFunction} to compute a descending sort expression.
         *
         * @param alias                      client-facing alias to register; must not be null or blank
         * @param fieldName                  entity field name the alias maps to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param <K>                        expression result type
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if alias is blank or already mapped to a different field, or if fieldName is blank
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addDescSort(
                @NonNull final String alias,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            addSort(alias, fieldName, expressionProviderFunction, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register a single client-facing alias that maps to the given field and attach an
         * {@link ExpressionProviderFunction} to compute both ascending and descending sort expressions.
         *
         * @param alias                      client-facing alias to register; must not be null or blank
         * @param fieldName                  entity field name the alias maps to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param <K>                        expression result type
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if alias is blank or already mapped to a different field, or if fieldName is blank
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSorts(
                @NonNull final String alias,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            addSort(alias, fieldName, expressionProviderFunction, Sort.Direction.ASC, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register multiple client-facing aliases that all map to the same entity field and
         * allow ascending sorting for that field.
         *
         * @param aliases   set of client-facing aliases to register; must not be null or empty
         * @param fieldName entity field name the aliases map to; must not be null or blank
         * @return this configurer for chaining
         * @throws NullPointerException     if aliases is null
         * @throws IllegalArgumentException if aliases is empty or any alias is blank or already mapped to a different field
         */
        public SortConfigurer<T> addAscSort(@NonNull final Set<String> aliases, @NonNull final String fieldName) {
            addSort(aliases, fieldName, Sort.Direction.ASC);
            return this;
        }

        /**
         * Register multiple client-facing aliases that all map to the same entity field and
         * allow descending sorting for that field.
         *
         * @param aliases   set of client-facing aliases to register; must not be null or empty
         * @param fieldName entity field name the aliases map to; must not be null or blank
         * @return this configurer for chaining
         * @throws NullPointerException     if aliases is null
         * @throws IllegalArgumentException if aliases is empty or any alias is blank or already mapped to a different field
         */
        public SortConfigurer<T> addDescSort(@NonNull final Set<String> aliases, @NonNull final String fieldName) {
            addSort(aliases, fieldName, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register multiple client-facing aliases that all map to the same entity field and
         * allow both ascending and descending sorting for that field.
         *
         * @param aliases   set of client-facing aliases to register; must not be null or empty
         * @param fieldName entity field name the aliases map to; must not be null or blank
         * @return this configurer for chaining
         * @throws NullPointerException     if aliases is null
         * @throws IllegalArgumentException if aliases is empty or any alias is blank or already mapped to a different field
         */
        public SortConfigurer<T> addSorts(@NonNull final Set<String> aliases, @NonNull final String fieldName) {
            addSort(aliases, fieldName, Sort.Direction.ASC, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register multiple client-facing aliases that map to the same entity field and attach an
         * {@link ExpressionProviderFunction} to compute the ascending sort expression for that field.
         *
         * @param aliases                    set of client-facing aliases to register; must not be null or empty
         * @param fieldName                  entity field name the aliases map to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param <K>                        expression result type
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if aliases is empty or any alias is blank or already mapped to a different field
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addAscSort(
                @NonNull final Set<String> aliases,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            addSort(aliases, fieldName, expressionProviderFunction, Sort.Direction.ASC);
            return this;
        }

        /**
         * Register multiple client-facing aliases that map to the same entity field and attach an
         * {@link ExpressionProviderFunction} to compute the descending sort expression for that field.
         *
         * @param aliases                    set of client-facing aliases to register; must not be null or empty
         * @param fieldName                  entity field name the aliases map to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param <K>                        expression result type
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if aliases is empty or any alias is blank or already mapped to a different field
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addDescSort(
                @NonNull final Set<String> aliases,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            addSort(aliases, fieldName, expressionProviderFunction, Sort.Direction.DESC);
            return this;
        }

        /**
         * Register multiple client-facing aliases that map to the same entity field and attach an
         * {@link ExpressionProviderFunction} to compute both ascending and descending sort expressions for that field.
         *
         * @param aliases                    set of client-facing aliases to register; must not be null or empty
         * @param fieldName                  entity field name the aliases map to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param <K>                        expression result type
         * @return this configurer for chaining
         * @throws NullPointerException     if any argument is null
         * @throws IllegalArgumentException if aliases is empty or any alias is blank or already mapped to a different field
         */
        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSorts(
                @NonNull final Set<String> aliases,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            addSort(aliases, fieldName, expressionProviderFunction, Sort.Direction.ASC, Sort.Direction.DESC);
            return this;
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
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if sortName is blank.
         */
        public SortConfigurer<T> addCustomSort(
                @NonNull final String sortName,
                @NonNull final CustomSortFunction<T> sortFunction
        ) {
            validateSortName(sortName);
            validateCustomSortFunction(sortFunction);
            addCustomSortHolder(sortName, sortFunction);
            return this;
        }

        /**
         * Create or update a {@link CustomSortHolder} for the given custom sort name.
         * <p>
         * If a holder already exists for {@code sortName}, the existing holder is returned and its
         * source types are updated (merged) with the current {@code sourceType}. The provided
         * {@code sortFunction} is only used when creating a new holder.
         *
         * @param sortName     the name of the custom sort
         * @param sortFunction the implementation of the custom sort
         */
        private void addCustomSortHolder(String sortName, CustomSortFunction<T> sortFunction) {
            var customSortHolder = templateBuilder.getCustomSorts()
                    .compute(sortName, (key, existingHolder) -> (
                            existingHolder != null ?
                                    existingHolder :
                                    new CustomSortHolder<>(sortFunction, EnumSet.noneOf(SourceType.class))
                    ));
            customSortHolder.sourceTypes().add(sourceType);
        }

        /**
         * Adds a sort for the specified field.
         * <p>
         * If a sort with the same {@code fieldName} already exists, the existing sort is used as-is
         * and the new directions are added to it. If no sort exists, a new one is created.
         *
         * @param fieldName  The name of the field to sort by. Must not be null.
         * @param directions The sort directions. Must not be null.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if fieldName or directions are null.
         * @throws IllegalArgumentException if fieldName is blank.
         */
        private SortConfigurer<T> addSort(
                @NonNull final String fieldName,
                @NonNull Sort.Direction... directions
        ) {
            addBaseSort(fieldName, directions);
            registerDefaultAlias(fieldName);
            return this;
        }

        /**
         * Adds a sort with a custom expression provider.
         * <p>
         * If a sort with the same {@code fieldName} already exists, this new definition will overwrite the existing
         * expression provider. The directions and source types will be merged.
         *
         * @param fieldName                  The name of the sort. Must not be null.
         * @param expressionProviderFunction A function that provides a custom JPA {@code Expression}. Must not be null.
         * @param directions                 The sort directions. Must not be null.
         * @param <K>                        The type of the expression result.
         * @return This configurer instance for chaining.
         * @throws NullPointerException     if any of the arguments are null.
         * @throws IllegalArgumentException if fieldName is blank.
         */
        private <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSort(
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
                @NonNull Sort.Direction... directions
        ) {
            addBaseSort(fieldName, expressionProviderFunction, directions);
            registerDefaultAlias(fieldName);
            return this;
        }

        /**
         * Internal helper for registering a single client-facing alias and adding the requested
         * directions for the target field. This method performs alias validation, updates the
         * {@link SortHolder} for the given {@code fieldName} (merging directions and source types),
         * and registers the alias -> field mapping.
         * <p>
         * Note: unlike the public addSort(String) variants this helper does not register
         * the default alias (i.e. fieldName -> fieldName) because it is used when an explicit
         * client-facing alias is provided.
         *
         * @param alias      client-facing alias to register; must not be null or blank
         * @param fieldName  entity field name the alias maps to; must not be null or blank
         * @param directions allowed sort directions to add for the field
         */
        private void addSort(
                @NonNull final String alias,
                @NonNull final String fieldName,
                @NonNull final Sort.Direction... directions
        ) {
            validateAlias(alias, fieldName);
            addSortHolder(fieldName, directions);
            addAliasToField(alias, fieldName);
        }

        /**
         * Internal helper for registering a single client-facing alias with a custom
         * {@link ExpressionProviderFunction} and adding the requested directions for the target field.
         * Validates the alias, attaches or replaces the expression provider for the field, merges
         * directions and source types, and registers the alias -> field mapping.
         *
         * @param alias                      client-facing alias to register; must not be null or blank
         * @param fieldName                  entity field name the alias maps to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param directions                 allowed sort directions to add for the field
         * @param <K>                        expression result type
         */
        private <K extends Comparable<? super K> & Serializable> void addSort(
                @NonNull final String alias,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
                @NonNull final Sort.Direction... directions
        ) {
            validateAlias(alias, fieldName);
            addSortHolder(fieldName, expressionProviderFunction, directions);
            addAliasToField(alias, fieldName);
        }

        /**
         * Internal helper for registering multiple client-facing aliases that all map to the same
         * entity field and adding the requested directions for that field. Validates the alias set
         * and registers each alias->field mapping after updating the {@link SortHolder}.
         *
         * @param aliases    set of client-facing aliases to register; must not be null or empty
         * @param fieldName  entity field name the aliases map to; must not be null or blank
         * @param directions allowed sort directions to add for the field
         */
        private void addSort(
                @NonNull final Set<String> aliases,
                @NonNull final String fieldName,
                @NonNull final Sort.Direction... directions
        ) {
            validateAliasesSet(aliases, fieldName);
            addSortHolder(fieldName, directions);
            aliases.forEach(alias -> addAliasToField(alias, fieldName));
        }

        /**
         * Internal helper for registering multiple client-facing aliases that map to the same
         * entity field and attaching a custom {@link ExpressionProviderFunction}. Validates the
         * alias set, attaches or replaces the expression provider for the field, merges directions
         * and source types, and registers each alias->field mapping.
         *
         * @param aliases                    set of client-facing aliases to register; must not be null or empty
         * @param fieldName                  entity field name the aliases map to; must not be null or blank
         * @param expressionProviderFunction function that provides the JPA expression; must not be null
         * @param directions                 allowed sort directions to add for the field
         * @param <K>                        expression result type
         */
        private <K extends Comparable<? super K> & Serializable> void addSort(
                @NonNull final Set<String> aliases,
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
                @NonNull final Sort.Direction... directions
        ) {
            validateAliasesSet(aliases, fieldName);
            addSortHolder(fieldName, expressionProviderFunction, directions);
            aliases.forEach(alias -> addAliasToField(alias, fieldName));
        }

        /**
         * Create or update a {@link SortHolder} for the given field name without registering any expression provider.
         * This merges directions and source types if a holder already exists.
         *
         * @param fieldName  entity field name
         * @param directions the directions to add to the holder
         * @param <K>        comparable type parameter for the holder's expression
         */
        private <K extends Comparable<? super K> & Serializable> void addSortHolder(
                @NonNull final String fieldName,
                @NonNull final Sort.Direction... directions
        ) {
            var sortHolder = templateBuilder.getSorts()
                    .compute(fieldName, (key, existingHolder) -> (
                            existingHolder != null ?
                                    existingHolder :
                                    new SortHolder<T, K>(EnumSet.noneOf(Sort.Direction.class), EnumSet.noneOf(SourceType.class), Optional.empty())
                    ));
            sortHolder.directions().addAll(Arrays.stream(directions).toList());
            sortHolder.sourceTypes().add(sourceType);
        }

        /**
         * Create or update a {@link SortHolder} for the given field name and attach a custom expression provider.
         * If a holder already exists this will replace its expression provider with the provided one while
         * merging directions and source types.
         *
         * @param fieldName                  entity field name
         * @param expressionProviderFunction expression provider to be attached
         * @param directions                 the directions to add to the holder
         * @param <K>                        comparable type parameter for the holder's expression
         */
        private <K extends Comparable<? super K> & Serializable> void addSortHolder(
                @NonNull final String fieldName,
                @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction,
                @NonNull final Sort.Direction... directions
        ) {
            var sortHolder = templateBuilder.getSorts()
                    .compute(fieldName, (key, existingHolder) -> (
                            existingHolder != null ?
                                    new SortHolder<>(EnumSet.copyOf(existingHolder.directions()), EnumSet.copyOf(existingHolder.sourceTypes()), Optional.of(expressionProviderFunction)) :
                                    new SortHolder<>(EnumSet.noneOf(Sort.Direction.class), EnumSet.noneOf(SourceType.class), Optional.of(expressionProviderFunction))
                    ));
            sortHolder.directions().addAll(Arrays.stream(directions).toList());
            sortHolder.sourceTypes().add(sourceType);
        }

        /**
         * Register the default alias where the alias equals the entity field name.
         * This preserves backwards compatibility for clients that use entity field names directly.
         *
         * @param fieldName the entity field name to register as its own alias
         */
        private void registerDefaultAlias(@NonNull final String fieldName) {
            templateBuilder.getAliasToField().putIfAbsent(fieldName, fieldName);
        }

        /**
         * Add a mapping from a client-facing alias to the actual entity field name.
         * The method does not perform validation itself; callers should validate alias uniqueness before invoking.
         *
         * @param alias     the client-facing alias
         * @param fieldName the entity field name
         */
        private void addAliasToField(String alias, String fieldName) {
            templateBuilder.getAliasToField().put(alias, fieldName);
        }
    }
}
