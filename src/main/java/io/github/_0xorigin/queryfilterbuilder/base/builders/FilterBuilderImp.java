package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import io.github._0xorigin.queryfilterbuilder.base.enumfield.AbstractEnumFilterField;
import io.github._0xorigin.queryfilterbuilder.base.enums.FilterType;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.FieldCaster;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.parsers.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.validators.FilterValidator;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of the FilterBuilder interface, responsible for constructing JPA Predicate objects from filter requests.
 *
 * @param <T> The type of the entity being filtered.
 */
public final class FilterBuilderImp<T> implements FilterBuilder<T> {

    private final PathGenerator<T> fieldPathGenerator;
    private final FilterParser filterParser;
    private final FilterFieldRegistry filterFieldRegistry;
    private final FilterOperatorRegistry filterOperatorRegistry;
    private final AbstractEnumFilterField enumFilterField;
    private final LocalizationService localizationService;
    private final Logger log = LoggerFactory.getLogger(FilterBuilderImp.class);

    /**
     * Constructs a new FilterBuilderImp with the necessary dependencies.
     *
     * @param fieldPathGenerator      Generator for creating JPA Path expressions from field names.
     * @param filterParser            Parser for extracting filter requests from the source.
     * @param filterFieldRegistry     Registry for available filter field types (e.g., String, Integer).
     * @param filterOperatorRegistry  Registry for available filter operators (e.g., EQ, GT).
     * @param enumFilterField         Enum filter field provider.
     * @param localizationService     Service for retrieving localized error messages.
     */
    public FilterBuilderImp(
        final PathGenerator<T> fieldPathGenerator,
        final FilterParser filterParser,
        final FilterFieldRegistry filterFieldRegistry,
        final FilterOperatorRegistry filterOperatorRegistry,
        final AbstractEnumFilterField enumFilterField,
        final LocalizationService localizationService
    ) {
        this.fieldPathGenerator = fieldPathGenerator;
        this.filterParser = filterParser;
        this.filterFieldRegistry = filterFieldRegistry;
        this.filterOperatorRegistry = filterOperatorRegistry;
        this.enumFilterField = enumFilterField;
        this.localizationService = localizationService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation parses filter data from either an {@code HttpServletRequest} (query parameters) or a list of
     * {@code FilterRequest} objects (request body), as provided in the {@code filterContext}. It consolidates the filters
     * into a distinct collection, ensuring that each filter field is processed only once.
     * <p>
     * If a filter is specified in both the query parameters and the request body, the value from the request body
     * will override the one from the query parameters. It also determines whether each filter is a normal or custom
     * filter based on the context's configuration.
     */
    @Override
    public Collection<FilterWrapper> getDistinctFilterWrappers(@NonNull final FilterContext<T> filterContext) {
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

    /**
     * {@inheritDoc}
     * <p>
     * This implementation determines whether the filter is a standard field filter or a custom filter by checking
     * the {@code filterType} of the {@code filterWrapper}. It then delegates to the appropriate private method
     * ({@code buildFilterPredicate} or {@code buildCustomFilterPredicate}) to construct the final JPA {@link Predicate}.
     * If the filter type is not set or not supported, it returns an empty optional.
     */
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

        final FilterErrorWrapper filterErrorWrapper = new FilterErrorWrapper(errorHolder.bindingResult(), filterWrapper);
        return switch (filterType.get()) {
            case NORMAL -> buildFilterPredicate(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder, filterErrorWrapper);
            case CUSTOM -> buildCustomFilterPredicate(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder, filterErrorWrapper);
            default -> Optional.empty();
        };
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> buildFilterPredicate(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        if (!isValidFilter(filterContext, filterWrapper))
            return Optional.empty();

        final Expression<K> expression = getExpression(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder);
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);

        final Class<? extends K> dataType = getFieldDataType(expression);
        final FilterOperator filterOperator = filterOperatorRegistry.getOperator(filterWrapper.operator());
        if (dataType.isEnum()) {
            List<K> enumValues = validateAndGetCastedEnumValues(dataType, filterOperator, filterWrapper, errorHolder, filterErrorWrapper);
            return filterOperator.apply(expression, criteriaBuilder, enumValues, filterErrorWrapper);
        }

        final AbstractFilterField<? extends Comparable<?>> filterField = getFilterField(dataType);
        final List<K> values = validateAndGetCastedValues(filterField, filterOperator, filterWrapper, errorHolder, filterErrorWrapper);
        return filterOperator.apply(expression, criteriaBuilder, values, filterErrorWrapper);
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> buildCustomFilterPredicate(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder cb,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        if (!isValidCustomFilter(filterContext, filterWrapper))
            return Optional.empty();

        @SuppressWarnings("unchecked")
        final CustomFilterHolder<T, K> customFilter = (CustomFilterHolder<T, K>)(filterContext.getCustomFilters().get(filterWrapper.originalFieldName()));
        final FilterOperator filterOperator = filterOperatorRegistry.getOperator(Operator.EQ);
        if (customFilter.dataType().isEnum()) {
            List<K> enumValues = validateAndGetCastedEnumValues(customFilter.dataType(), filterOperator, filterWrapper, errorHolder, filterErrorWrapper);
            return customFilter.customFilterFunction().apply(root, criteriaQuery, cb, enumValues, filterErrorWrapper);
        }

        final AbstractFilterField<? extends Comparable<?>> filterField = getFilterField(customFilter.dataType());
        final List<K> values = validateAndGetCastedValues(filterField, filterOperator, filterWrapper, errorHolder, filterErrorWrapper);
        return customFilter.customFilterFunction().apply(root, criteriaQuery, cb, values, filterErrorWrapper);
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
        return providerFunction.orElseGet(() -> fieldPathGenerator.generate(root, filterWrapper.field(), filterWrapper.originalFieldName(), errorHolder.bindingResult()));
    }

    private <K extends Comparable<? super K> & Serializable> Class<? extends K> getFieldDataType(final Expression<K> expression) {
        return expression.getJavaType();
    }

    private <K extends Comparable<? super K> & Serializable> AbstractFilterField<? extends Comparable<?>> getFilterField(final Class<K> dataType) {
        return filterFieldRegistry.getFilterField(dataType);
    }

    private <K extends Comparable<? super K> & Serializable> List<K> validateAndGetCastedValues(
        final AbstractFilterField<? extends Comparable<?>> filterField,
        final FilterOperator filterOperator,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        FilterValidator.validateFilterFieldAndOperator(
            filterField,
            filterOperator,
            filterWrapper,
            filterErrorWrapper,
            localizationService
        );
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);

        final FieldCaster<K> fieldCaster = getFieldCaster(filterField);
        return getCastedValues(filterWrapper, fieldCaster, filterErrorWrapper);
    }

    private <K extends Comparable<? super K> & Serializable> List<K> validateAndGetCastedEnumValues(
        final Class<? extends K> dataType,
        final FilterOperator filterOperator,
        final FilterWrapper filterWrapper,
        final ErrorHolder errorHolder,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        FilterValidator.validateEnumFilterOperator(
            enumFilterField,
            filterOperator,
            filterWrapper,
            filterErrorWrapper,
            localizationService
        );
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);
        return getCastedEnumValues(filterWrapper, dataType, filterErrorWrapper);
    }

    @SuppressWarnings("unchecked")
    private <K extends Comparable<? super K> & Serializable> FieldCaster<K> getFieldCaster(final AbstractFilterField<? extends Comparable<?>> filterField) {
        return (FieldCaster<K>) filterField;
    }

    private <K extends Comparable<? super K> & Serializable> List<K> getCastedValues(
        final FilterWrapper filterWrapper,
        final FieldCaster<K> filterCaster,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        return filterWrapper.values()
            .stream()
            .map(value -> filterCaster.safeCast(value, filterErrorWrapper))
            .toList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <K extends Comparable<? super K> & Serializable> List<K> getCastedEnumValues(
        final FilterWrapper filterWrapper,
        final Class<? extends K> enumClass,
        final FilterErrorWrapper filterErrorWrapper
    ) {
        return filterWrapper.values()
            .stream()
            .map(value ->  (K)(enumFilterField.safeCast((Class<? extends Enum>) enumClass, value, filterErrorWrapper)))
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
