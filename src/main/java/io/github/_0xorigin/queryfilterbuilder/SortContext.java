package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.dtos.SortRequest;
import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.CustomSortFunction;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomSortHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.SortHolder;
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

    private SortContext(@NonNull final Builder<T> builder) {
        this.sorts = Map.copyOf(builder.getSorts());
        this.customSorts = Map.copyOf(builder.getCustomSorts());
        this.request = builder.getRequest().orElse(null);
        this.sortRequests = builder.getSortRequests().orElse(null);
    }

    public static <T> Builder<T> buildForType(@NonNull Class<T> type) {
        Objects.requireNonNull(type, "Type must not be null");
        return builder();
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
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

    public static final class Builder<T> {
        private final Map<String, SortHolder<T, ?>> sorts = new HashMap<>();
        private final Map<String, CustomSortHolder<T>> customSorts = new HashMap<>();
        private HttpServletRequest request;
        private List<SortRequest> sortRequests;

        private Builder() {}

        public Builder<T> queryParam(
            @NonNull final HttpServletRequest request,
            @NonNull final Consumer<SortConfigurer<T>> configurer
        ) {
            Objects.requireNonNull(request, "HttpServletRequest must not be null");
            Objects.requireNonNull(configurer, "Consumer for SortConfigurer must not be null");
            this.request = Optional.ofNullable(this.request).orElse(request);
            SortConfigurer<T> sortConfigurer = new SortConfigurer<>(this, SourceType.QUERY_PARAM);
            configurer.accept(sortConfigurer);
            return this;
        }

        public Builder<T> requestBody(
            @NonNull final List<SortRequest> sorts,
            @NonNull final Consumer<SortConfigurer<T>> configurer
        ) {
            Objects.requireNonNull(sorts, "SortRequest list must not be null");
            Objects.requireNonNull(configurer, "Consumer for SortConfigurer must not be null");
            this.sortRequests = Optional.ofNullable(this.sortRequests).orElse(sorts);
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

        private Optional<HttpServletRequest> getRequest() {
            return Optional.ofNullable(request);
        }

        private Optional<List<SortRequest>> getSortRequests() {
            return Optional.ofNullable(sortRequests);
        }

        public SortContext<T> build() {
            return new SortContext<>(this);
        }
    }

    public static class SortConfigurer<T> {
        private final Builder<T> builder;
        private final SourceType sourceType;

        private SortConfigurer(@NonNull Builder<T> builder, @NonNull SourceType sourceType) {
            this.builder = builder;
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

            var customSortHolder = builder.getCustomSorts()
                .compute(sortName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new CustomSortHolder<>(existingHolder.customSortFunction(), EnumSet.copyOf(existingHolder.sourceTypes())) :
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

            var sortHolder = builder.getSorts()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new SortHolder<>(EnumSet.copyOf(existingHolder.directions()), EnumSet.copyOf(existingHolder.sourceTypes()), existingHolder.expressionProviderFunction()) :
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

            var sortHolder = builder.getSorts()
                .compute(fieldName, (key, existingHolder) -> (
                    existingHolder != null ?
                        new SortHolder<>(EnumSet.copyOf(existingHolder.directions()), EnumSet.copyOf(existingHolder.sourceTypes()), existingHolder.expressionProviderFunction()) :
                        new SortHolder<>(EnumSet.noneOf(Sort.Direction.class), EnumSet.noneOf(SourceType.class), Optional.of(expressionProviderFunction))
                ));
            sortHolder.directions().addAll(Arrays.stream(directions).toList());
            sortHolder.sourceTypes().add(sourceType);
            return this;
        }
    }
}
