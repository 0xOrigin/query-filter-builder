package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import io.github._0xorigin.queryfilterbuilder.base.enums.FilterType;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.FieldCaster;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.parsers.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.validators.FilterValidator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

public final class FilterBuilderImp<T> implements FilterBuilder<T> {
    private final PathGenerator<T> fieldPathGenerator;
    private final FilterParser filterParser;
    private final FilterFieldRegistry filterFieldRegistry;
    private final FilterOperatorRegistry filterOperatorRegistry;
    private final LocalizationService localizationService;
    private final Logger log = LoggerFactory.getLogger(FilterBuilderImp.class);

    public FilterBuilderImp(
        final PathGenerator<T> fieldPathGenerator,
        final FilterParser filterParser,
        final FilterFieldRegistry filterFieldRegistry,
        final FilterOperatorRegistry filterOperatorRegistry,
        final LocalizationService localizationService
    ) {
        this.fieldPathGenerator = fieldPathGenerator;
        this.filterParser = filterParser;
        this.filterFieldRegistry = filterFieldRegistry;
        this.filterOperatorRegistry = filterOperatorRegistry;
        this.localizationService = localizationService;
    }

    @Override
    public Collection<FilterWrapper> getDistinctFilterWrappers(final FilterContext<T> filterContext) {
        final Map<String, FilterWrapper> filterWrappers = new LinkedHashMap<>();
        filterContext.getRequest().ifPresent(request ->
            filterParser.parse(request).forEach(filterWrapper ->
                filterWrappers.compute(filterWrapper.field(), (k, currentValue) -> setFilterType(filterContext, filterWrapper, currentValue))
            )
        );
        filterContext.getFilterRequests().ifPresent(filterRequests ->
            filterParser.parse(filterRequests).forEach(filterWrapper ->
                filterWrappers.compute(filterWrapper.originalFieldName(), (k, currentValue) -> setFilterType(filterContext, filterWrapper, currentValue))
            )
        );
//        log.debug("FilterWrappers: {}", filterWrappers);
        return filterWrappers.values();
    }

    @Override
    public Optional<Predicate> buildPredicateForWrapper(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder
    ) {
        final Optional<FilterType> filterType = filterWrapper.filterType();
        if (filterType.isEmpty())
            return Optional.empty();

        return switch (filterType.get()) {
            case NORMAL -> buildFilterPredicate(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder);
            case CUSTOM -> buildCustomFilterPredicate(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder);
            default -> Optional.empty();
        };
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> buildFilterPredicate(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder
    ) {
        if (!isValidFilter(filterContext, filterWrapper))
            return Optional.empty();

        final Expression<K> expression = getExpression(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder);
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);

        final Class<? extends K> dataType = getFieldDataType(expression);
        final AbstractFilterField<? extends Comparable<?>> filterField = getFilterField(dataType);
        final FilterOperator filterOperator = filterOperatorRegistry.getOperator(filterWrapper.operator());

        FilterValidator.validateFilterFieldAndOperator(
            filterField,
            filterOperator,
            filterWrapper,
            new FilterErrorWrapper(errorHolder.bindingResult(), filterWrapper),
            localizationService
        );
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);

        final FieldCaster<K> fieldCaster = getFieldCaster(filterField);
        final List<K> values = getCastedValues(filterWrapper, fieldCaster, errorHolder);
        return filterOperator.apply(expression, criteriaBuilder, values, new FilterErrorWrapper(errorHolder.bindingResult(), filterWrapper));
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> buildCustomFilterPredicate(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder cb,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder
    ) {
        if (!isValidCustomFilter(filterContext, filterWrapper))
            return Optional.empty();

        final CustomFilterHolder<T, ?> customFilter = filterContext.getCustomFilters().get(filterWrapper.originalFieldName());
        final AbstractFilterField<? extends Comparable<?>> filterField = getFilterField(customFilter.dataType());
        final FilterOperator filterOperator = filterOperatorRegistry.getOperator(Operator.EQ);
        FilterValidator.validateFilterFieldAndOperator(
            filterField,
            filterOperator,
            filterWrapper,
            new FilterErrorWrapper(errorHolder.bindingResult(), filterWrapper),
            localizationService
        );
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);

        final FieldCaster<K> fieldCaster = getFieldCaster(filterField);
        final List<K> values = getCastedValues(filterWrapper, fieldCaster, errorHolder);
        return customFilter.customFilterFunction().apply(root, criteriaQuery, cb, values, new FilterErrorWrapper(errorHolder.bindingResult(), filterWrapper));
    }

    private <K extends Comparable<? super K> & Serializable> Expression<K> getExpression(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder
    ) {
        var filterHolder = filterContext.getFilters().get(filterWrapper.field());
        Optional<Expression<K>> providerFunction = filterHolder.getExpression(root, criteriaQuery, criteriaBuilder);
        return providerFunction.orElse(fieldPathGenerator.generate(root, filterWrapper.field(), filterWrapper.originalFieldName(), errorHolder.bindingResult()));
    }

    private <K extends Comparable<? super K> & Serializable> Class<? extends K> getFieldDataType(final Expression<K> expression) {
        return expression.getJavaType();
    }

    private <K extends Comparable<? super K> & Serializable> AbstractFilterField<? extends Comparable<?>> getFilterField(final Class<K> dataType) {
        return filterFieldRegistry.getFilterField(dataType);
    }

    @SuppressWarnings("unchecked")
    private <K extends Comparable<? super K> & Serializable> FieldCaster<K> getFieldCaster(final AbstractFilterField<? extends Comparable<?>> filterField) {
        return (FieldCaster<K>) filterField;
    }

    private <K extends Comparable<? super K> & Serializable> List<K> getCastedValues(
        final FilterWrapper filterWrapper,
        final FieldCaster<K> filterCaster,
        final ErrorHolder errorHolder
    ) {
        return filterWrapper.values()
            .stream()
            .map(value -> filterCaster.safeCast(value, new FilterErrorWrapper(errorHolder.bindingResult(), filterWrapper)))
            .toList();
    }

    private FilterWrapper setFilterType(final FilterContext<T> filterContext, final FilterWrapper filterWrapper, final FilterWrapper currentValue) {
        if (isValidFilter(filterContext, filterWrapper))
            return filterWrapper.withFilterType(FilterType.NORMAL);

        if (isValidCustomFilter(filterContext, filterWrapper))
            return filterWrapper.withFilterType(FilterType.CUSTOM);

        return currentValue;
    }

    private boolean isValidFilter(final FilterContext<T> filterContext, final FilterWrapper filterWrapper) {
        final var filters = filterContext.getFilters();
        final boolean isFilterExists = filters.containsKey(filterWrapper.field());
        final var filterHolder = filters.get(filterWrapper.field());
        return (
            isFilterExists
                && filterHolder.operators().contains(filterWrapper.operator())
                && filterHolder.sourceTypes().contains(filterWrapper.sourceType())
        );
    }

    private boolean isValidCustomFilter(final FilterContext<T> filterContext, final FilterWrapper filterWrapper) {
        final var customFilters = filterContext.getCustomFilters();
        final boolean isFilterExists = customFilters.containsKey(filterWrapper.originalFieldName());
        final var customFilterHolder = customFilters.get(filterWrapper.originalFieldName());
        return (
            isFilterExists
                && customFilterHolder.sourceTypes().contains(filterWrapper.sourceType())
        );
    }
}
