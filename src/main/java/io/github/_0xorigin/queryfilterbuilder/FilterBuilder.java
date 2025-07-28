package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.QueryFilterBuilder;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomFilterHolder;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidFilterConfigurationException;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryFilterValueException;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public final class FilterBuilder<T> implements QueryFilterBuilder<T> {
    private final FilterParser filterParser;
    private final PathGenerator<T> filterPathGenerator;
    private final FilterRegistry filterRegistry;
    private final FilterOperatorRegistry filterOperatorRegistry;
    private final Logger log = LoggerFactory.getLogger(FilterBuilder.class);

    public FilterBuilder(
        final FilterParser filterParser,
        final PathGenerator<T> filterPathGenerator,
        final FilterRegistry filterRegistry,
        final FilterOperatorRegistry filterOperatorRegistry
    ) {
        this.filterParser = filterParser;
        this.filterPathGenerator = filterPathGenerator;
        this.filterRegistry = filterRegistry;
        this.filterOperatorRegistry = filterOperatorRegistry;
    }

    @Override
    public Specification<T> buildFilterSpecification(final FilterContext<T> filterContext) {
        final BindingResult bindingResult = getBindingResult();
        return (root, criteriaQuery, criteriaBuilder) -> {
            final Set<Predicate> predicates = getFilterWrappers(filterContext).stream()
                    .map(filterWrapper -> buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, bindingResult, filterContext, filterWrapper))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toUnmodifiableSet());
            log.info("Predicates: {}", predicates.size());
            throwClientSideExceptionIfInvalid(bindingResult);
            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Set<FilterWrapper> getFilterWrappers(final FilterContext<T> filterContext) {
        final Set<FilterWrapper> filterWrappers = new HashSet<>();
        filterContext.getRequest().ifPresent(request -> filterWrappers.addAll(filterParser.parse(request)));
        filterContext.getFilterRequests().ifPresent(filterRequests -> filterWrappers.addAll(filterParser.parse(filterRequests)));
        log.info("FilterWrappers: {}", filterWrappers);
        return filterWrappers;
    }

    private Optional<Predicate> buildPredicateForWrapper(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder cb,
        final BindingResult bindingResult,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper
    ) {
        final Optional<Predicate> customFilterPredicate = buildCustomFilterPredicate(root, criteriaQuery, cb, bindingResult, filterContext, filterWrapper);
        if (customFilterPredicate.isPresent())
            return customFilterPredicate;

        if (isValidFieldOperator(filterContext, filterWrapper))
            return createPredicate(root, cb, bindingResult, filterWrapper);

        return Optional.empty();
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> buildCustomFilterPredicate(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder cb,
        final BindingResult bindingResult,
        final FilterContext<T> filterContext,
        final FilterWrapper filterWrapper
    ) {
        if (!isValidCustomField(filterContext, filterWrapper))
            return Optional.empty();

        final CustomFilterHolder<T, ?> customFilter = filterContext.getCustomFilters().get(filterWrapper.originalFieldName());
        final AbstractFilterField<? extends Comparable<?>> filterClass = getFieldFilter(customFilter.dataType());
        final FilterOperator filterOperator = filterOperatorRegistry.getOperator(Operator.EQ);
        FilterValidator.validateFilterFieldAndOperator(
            filterClass,
            filterOperator,
            filterWrapper,
            new ErrorWrapper(bindingResult, filterWrapper)
        );
        throwServerSideExceptionIfInvalid(bindingResult);

        final List<K> values = filterWrapper.values().stream()
                .map(value -> (K) filterClass.safeCast(value, new ErrorWrapper(bindingResult, filterWrapper)))
                .toList();
        return customFilter.customFilterFunction().apply(root, criteriaQuery, cb, values, new ErrorWrapper(bindingResult, filterWrapper));
    }

    private boolean isValidFieldOperator(final FilterContext<T> filterContext, final FilterWrapper filterWrapper) {
        final var filters = filterContext.getFilters();
        final boolean isFilterExists = filters.containsKey(filterWrapper.field());
        final var filterHolder = filters.get(filterWrapper.field());
        return (
            isFilterExists
            && filterHolder.operators().contains(filterWrapper.operator())
            && filterHolder.sourceTypes().contains(filterWrapper.sourceType())
        );
    }

    private boolean isValidCustomField(final FilterContext<T> filterContext, final FilterWrapper filterWrapper) {
        final var customFilters = filterContext.getCustomFilters();
        final boolean isFilterExists = customFilters.containsKey(filterWrapper.originalFieldName());
        final var customFilterHolder = customFilters.get(filterWrapper.originalFieldName());
        return (
            isFilterExists
            && customFilterHolder.sourceTypes().contains(filterWrapper.sourceType())
        );
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> createPredicate(
        final Root<T> root,
        final CriteriaBuilder builder,
        final BindingResult bindingResult,
        final FilterWrapper filterWrapper
    ) {
        final Expression<K> expression = getExpression(root, filterWrapper, bindingResult);
        throwServerSideExceptionIfInvalid(bindingResult);

        final Class<? extends K> dataType = getFieldDataType(expression);
        final AbstractFilterField<? extends Comparable<?>> filterClass = getFieldFilter(dataType);
        final FilterOperator filterOperator = filterOperatorRegistry.getOperator(filterWrapper.operator());

        FilterValidator.validateFilterFieldAndOperator(
            filterClass,
            filterOperator,
            filterWrapper,
            new ErrorWrapper(bindingResult, filterWrapper)
        );
        throwServerSideExceptionIfInvalid(bindingResult);

        final List<K> values = filterWrapper.values()
                .stream()
                .map(value -> (K) filterClass.safeCast(value, new ErrorWrapper(bindingResult, filterWrapper)))
                .toList();
        return filterOperator.apply(expression, builder, values, new ErrorWrapper(bindingResult, filterWrapper));
    }

    private BindingResult getBindingResult() {
        return new BeanPropertyBindingResult(this, "queryFilterBuilder");
    }

    private <K extends Comparable<? super K> & Serializable> Expression<K> getExpression(final Root<T> root, final FilterWrapper filterWrapper, final BindingResult bindingResult) {
        return filterPathGenerator.generate(root, filterWrapper.field(), new ErrorWrapper(bindingResult, filterWrapper));
    }

    private <K extends Comparable<? super K> & Serializable> Class<? extends K> getFieldDataType(final Expression<K> expression) {
        return expression.getJavaType();
    }

    private <K extends Comparable<? super K> & Serializable> AbstractFilterField<? extends Comparable<?>> getFieldFilter(final Class<K> dataType) {
        return filterRegistry.getFieldFilter(dataType);
    }

    private MethodArgumentNotValidException getMethodArgumentNotValidException(final BindingResult bindingResult) {
        try {
            return new MethodArgumentNotValidException(
                new MethodParameter(
                    this.getClass()
                        .getMethod(
                            "buildFilterSpecification",
                            FilterContext.class
                        ),
                0
                ),
                bindingResult
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void throwClientSideExceptionIfInvalid(final BindingResult bindingResult) {
        if (!bindingResult.hasErrors())
            return;

        throw new InvalidQueryFilterValueException(
            getMethodArgumentNotValidException(bindingResult)
        );
    }

    private void throwServerSideExceptionIfInvalid(final BindingResult bindingResult) {
        if (!bindingResult.hasErrors())
            return;

        throw new InvalidFilterConfigurationException(
            getMethodArgumentNotValidException(bindingResult)
        );
    }
}
