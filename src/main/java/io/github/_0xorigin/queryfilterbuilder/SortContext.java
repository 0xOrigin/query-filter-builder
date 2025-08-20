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

public final class SortContext<T> {
    private final Map<String, SortHolder<T, ?>> sorts;
    private final Map<String, CustomSortHolder<T>> customSorts;
    private final HttpServletRequest request;
    private final List<SortRequest> sortRequests;

    private SortContext(@NonNull final SourceBuilder<T> sourceBuilder) {
        this.sorts = Map.copyOf(sourceBuilder.getTemplate().getSorts());
        this.customSorts = Map.copyOf(sourceBuilder.getTemplate().getCustomSorts());
        this.request = sourceBuilder.getRequest().orElse(null);
        this.sortRequests = sourceBuilder.getSortRequests().orElse(null);
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

    public Map<String, SortHolder<T, ?>> getSorts() {
        return sorts;
    }

    public Map<String, CustomSortHolder<T>> getCustomSorts() {
        return customSorts;
    }

    public Optional<HttpServletRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    public Optional<List<SortRequest>> getSortRequests() {
        return Optional.ofNullable(sortRequests);
    }

    public static final class SourceBuilder<T> {
        private final Template<T> template;
        private HttpServletRequest request;
        private List<SortRequest> sortRequests;

        private SourceBuilder(@NonNull final Template<T> template) {
            this.template = template;
        }

        public SourceBuilder<T> withQuerySource(@NonNull final HttpServletRequest request) {
            Objects.requireNonNull(request, "HttpServletRequest must not be null");
            this.request = request;
            return this;
        }

        public SourceBuilder<T> withBodySource(@NonNull final List<SortRequest> sortRequests) {
            Objects.requireNonNull(sortRequests, "SortRequests must not be null");
            this.sortRequests = sortRequests;
            return this;
        }

        public SortContext<T> buildSortContext() {
            return new SortContext<>(this);
        }

        private Template<T> getTemplate() {
            return template;
        }

        private Optional<HttpServletRequest> getRequest() {
            return Optional.ofNullable(request);
        }

        private Optional<List<SortRequest>> getSortRequests() {
            return Optional.ofNullable(sortRequests);
        }
    }

    public static final class Template<T> {
        private final Map<String, SortHolder<T, ?>> sorts;
        private final Map<String, CustomSortHolder<T>> customSorts;

        private Template(TemplateBuilder<T> templateBuilder) {
            this.sorts = Map.copyOf(templateBuilder.getSorts());
            this.customSorts = Map.copyOf(templateBuilder.getCustomSorts());
        }

        private Map<String, SortHolder<T, ?>> getSorts() {
            return sorts;
        }

        private Map<String, CustomSortHolder<T>> getCustomSorts() {
            return customSorts;
        }

        public SourceBuilder<T> newSourceBuilder() {
            return new SourceBuilder<>(this);
        }
    }

    public static final class TemplateBuilder<T> {
        private final Map<String, SortHolder<T, ?>> sorts = new HashMap<>();
        private final Map<String, CustomSortHolder<T>> customSorts = new HashMap<>();

        private TemplateBuilder() {}

        public TemplateBuilder<T> queryParam(@NonNull final Consumer<SortConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for SortConfigurer must not be null");
            SortConfigurer<T> sortConfigurer = new SortConfigurer<>(this, SourceType.QUERY_PARAM);
            configurer.accept(sortConfigurer);
            return this;
        }

        public TemplateBuilder<T> requestBody(@NonNull final Consumer<SortConfigurer<T>> configurer) {
            Objects.requireNonNull(configurer, "Consumer for SortConfigurer must not be null");
            SortConfigurer<T> sortConfigurer = new SortConfigurer<>(this, SourceType.REQUEST_BODY);
            configurer.accept(sortConfigurer);
            return this;
        }

        private Map<String, SortHolder<T, ?>> getSorts() {
            return sorts;
        }

        private Map<String, CustomSortHolder<T>> getCustomSorts() {
            return customSorts;
        }

        public Template<T> buildTemplate() {
            return new Template<>(this);
        }
    }

    public static class SortConfigurer<T> {
        private final TemplateBuilder<T> templateBuilder;
        private final SourceType sourceType;

        private SortConfigurer(@NonNull TemplateBuilder<T> templateBuilder, @NonNull SourceType sourceType) {
            this.templateBuilder = templateBuilder;
            this.sourceType = sourceType;
        }

        public SortConfigurer<T> addAscSort(@NonNull final String fieldName) {
            return addSort(fieldName, Sort.Direction.ASC);
        }

        public SortConfigurer<T> addDescSort(@NonNull final String fieldName) {
            return addSort(fieldName, Sort.Direction.DESC);
        }

        public SortConfigurer<T> addSorts(@NonNull final String fieldName) {
            return addSort(fieldName, Sort.Direction.ASC, Sort.Direction.DESC);
        }

        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addAscSort(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.ASC);
        }

        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addDescSort(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.DESC);
        }

        public <K extends Comparable<? super K> & Serializable> SortConfigurer<T> addSorts(
            @NonNull final String fieldName,
            @NonNull final ExpressionProviderFunction<T, K> expressionProviderFunction
        ) {
            return addSort(fieldName, expressionProviderFunction, Sort.Direction.ASC, Sort.Direction.DESC);
        }

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
