package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.*;
import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.CustomFilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidFilterConfigurationException;
import io.github._0xorigin.queryfilterbuilder.exceptions.InvalidQueryFilterValueException;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterRegistry;
import jakarta.persistence.criteria.*;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public final class FilterBuilder<T> implements QueryFilterBuilder<T> {

    private final Parser filterParser;
    private final PathGenerator<T> filterPathGenerator;
    private final FilterRegistry filterRegistry;
    private final FilterOperatorRegistry filterOperatorRegistry;

    public FilterBuilder(
        Parser filterParser,
        PathGenerator<T> filterPathGenerator,
        FilterRegistry filterRegistry,
        FilterOperatorRegistry filterOperatorRegistry
    ) {
        this.filterParser = filterParser;
        this.filterPathGenerator = filterPathGenerator;
        this.filterRegistry = filterRegistry;
        this.filterOperatorRegistry = filterOperatorRegistry;
    }

    @Override
    public Specification<T> buildFilterSpecification(FilterContext<T> filterContext) {
        BindingResult bindingResult = new BeanPropertyBindingResult(this, "queryFilterBuilder");
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = filterParser.parse().stream()
                    .map(filterWrapper -> buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, bindingResult, filterContext, filterWrapper))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            throwClientSideExceptionIfInvalid(bindingResult);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Optional<Predicate> buildPredicateForWrapper(
            Root<T> root,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder cb,
            BindingResult bindingResult,
            FilterContext<T> filterContext,
            FilterWrapper filterWrapper
    ) {
        Optional<Predicate> customPredicate = buildCustomFieldPredicate(root, criteriaQuery, cb, bindingResult, filterContext, filterWrapper);
        if (customPredicate.isPresent())
            return customPredicate;

        if (isValidFieldOperator(filterContext, filterWrapper))
            return createPredicate(root, cb, bindingResult, filterWrapper);

        return Optional.empty();
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> buildCustomFieldPredicate(
            Root<T> root,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder cb,
            BindingResult bindingResult,
            FilterContext<T> filterContext,
            FilterWrapper filterWrapper
    ) {
        CustomFilterWrapper<T, ?> customFilter = filterContext.getCustomFieldFilters().get(filterWrapper.originalFieldName());
        if (customFilter == null)
            return Optional.empty();

        FilterOperator filterOperator = filterOperatorRegistry.getOperator(Operator.EQ);
        AbstractFilterField<?> filterClass = getFieldFilter(customFilter.dataType());
        FilterValidator.validateFilterFieldAndOperator(
            filterClass,
            filterOperator,
            filterWrapper,
            new ErrorWrapper(bindingResult, filterWrapper)
        );
        throwServerSideExceptionIfInvalid(bindingResult);

        List<K> values = filterWrapper.values().stream()
                .map(value -> (K) filterClass.safeCast(value, new ErrorWrapper(bindingResult, filterWrapper)))
                .toList();
        return customFilter.customFilterFunction().apply(root, criteriaQuery, cb, values, new ErrorWrapper(bindingResult, filterWrapper));
    }

    private boolean isValidFieldOperator(FilterContext<T> filterContext, FilterWrapper filterWrapper) {
        return filterContext.getFieldOperators().containsKey(filterWrapper.field()) &&
                filterContext.getFieldOperators().get(filterWrapper.field()).contains(filterWrapper.operator());
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Predicate> createPredicate(
            Root<T> root,
            CriteriaBuilder builder,
            BindingResult bindingResult,
            FilterWrapper filterWrapper
    ) {
        Expression<K> expression = getExpression(root, filterWrapper, bindingResult);
        throwServerSideExceptionIfInvalid(bindingResult);

        Class<? extends K> dataType = getFieldDataType(expression);
        AbstractFilterField<?> filterClass = getFieldFilter(dataType);
        FilterOperator filterOperator = filterOperatorRegistry.getOperator(filterWrapper.operator());

        FilterValidator.validateFilterFieldAndOperator(
                filterClass,
                filterOperator,
                filterWrapper,
                new ErrorWrapper(bindingResult, filterWrapper)
        );
        throwServerSideExceptionIfInvalid(bindingResult);

        List<K> values = filterWrapper.values()
                .stream()
                .map(value -> (K) filterClass.safeCast(value, new ErrorWrapper(bindingResult, filterWrapper)))
                .toList();
        return filterOperator.apply(expression, builder, values, new ErrorWrapper(bindingResult, filterWrapper));
    }

    private <K extends Comparable<? super K> & Serializable> Expression<K> getExpression(Root<T> root, FilterWrapper filterWrapper, BindingResult bindingResult) {
        return filterPathGenerator.generate(root, filterWrapper.field(), new ErrorWrapper(bindingResult, filterWrapper));
    }

    private <K extends Comparable<? super K> & Serializable> Class<? extends K> getFieldDataType(Expression<K> expression) {
        return expression.getJavaType();
    }

    private <K extends Comparable<? super K> & Serializable> AbstractFilterField<?> getFieldFilter(Class<K> dataType) {
        return filterRegistry.getFieldFilter(dataType);
    }

    private void throwClientSideExceptionIfInvalid(BindingResult bindingResult) {
        try {
            if (!bindingResult.hasErrors())
                return;

            throw new InvalidQueryFilterValueException(
                new MethodArgumentNotValidException(
                    new MethodParameter(
                        this.getClass()
                            .getMethod(
                                "buildFilterSpecification",
                                FilterContext.class
                            ),
                    0
                    )
                    ,bindingResult
                )
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void throwServerSideExceptionIfInvalid(BindingResult bindingResult) {
        try {
            if (!bindingResult.hasErrors())
                return;

            throw new InvalidFilterConfigurationException(
                new MethodArgumentNotValidException(
                    new MethodParameter(
                        this.getClass()
                            .getMethod(
                                "buildFilterSpecification",
                                FilterContext.class
                            ),
                    0
                    )
                    ,bindingResult
                )
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
