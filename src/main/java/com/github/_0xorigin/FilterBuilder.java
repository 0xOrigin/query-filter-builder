package com.github._0xorigin;

import com.github._0xorigin.base.*;
import com.github._0xorigin.exceptions.InvalidFilterConfigurationException;
import com.github._0xorigin.exceptions.InvalidQueryFilterValueException;
import com.github._0xorigin.registries.FilterOperatorRegistry;
import com.github._0xorigin.registries.FilterRegistry;
import jakarta.persistence.criteria.*;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

public class FilterBuilder<T> implements QueryFilterBuilder<T> {

    private final Parser filterParser;
    private final PathGenerator<T> filterPathGenerator;
    private final FilterValidator filterValidator;

    public FilterBuilder(
            Parser filterParser,
            PathGenerator<T> filterPathGenerator,
            FilterValidator filterValidator
    ) {
        this.filterParser = filterParser;
        this.filterPathGenerator = filterPathGenerator;
        this.filterValidator = filterValidator;
    }

    public Predicate buildFilterPredicate(
            Root<T> root,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder cb,
            FilterContext<T> filterContext
    ) {
        BindingResult bindingResult = new BeanPropertyBindingResult(this, "queryFilterBuilder");
        List<Predicate> predicates = filterParser.parse().stream()
                .map(filterWrapper -> buildPredicateForWrapper(root, criteriaQuery, cb, bindingResult, filterContext, filterWrapper))
                .filter(Objects::nonNull)
                .toList();

        throwClientSideExceptionIfInvalid(bindingResult);
        return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildPredicateForWrapper(
            Root<T> root,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder cb,
            BindingResult bindingResult,
            FilterContext<T> filterContext,
            FilterWrapper filterWrapper
    ) {
        Predicate customPredicate = buildCustomFieldPredicate(root, criteriaQuery, cb, bindingResult, filterContext, filterWrapper);
        if (customPredicate != null)
            return customPredicate;

        if (isValidFieldOperator(filterContext, filterWrapper))
            return createPredicate(root, cb, bindingResult, filterWrapper);

        return null;
    }

    private Predicate buildCustomFieldPredicate(
            Root<T> root,
            CriteriaQuery<?> criteriaQuery,
            CriteriaBuilder cb,
            BindingResult bindingResult,
            FilterContext<T> filterContext,
            FilterWrapper filterWrapper
    ) {
        CustomFilterWrapper<T> customFilter = filterContext.getCustomFieldFilters().get(filterWrapper.getOriginalFieldName());
        if (customFilter == null)
            return null;

        FilterOperator filterOperator = FilterOperatorRegistry.getOperator(Operator.EQ);
        filterValidator.validateFilterFieldAndOperator(
                customFilter.getFilterField(),
                filterOperator,
                filterWrapper,
                new ErrorWrapper(bindingResult, filterWrapper)
        );
        throwServerSideExceptionIfInvalid(bindingResult);

        List<?> values = filterWrapper.getValues().stream()
                .map(value -> customFilter.getFilterField().cast(value, new ErrorWrapper(bindingResult, filterWrapper)))
                .toList();
        return customFilter.getCustomFilterFunction().apply(root, criteriaQuery, cb, values, new ErrorWrapper(bindingResult, filterWrapper));
    }

    private boolean isValidFieldOperator(FilterContext<T> filterContext, FilterWrapper filterWrapper) {
        return filterContext.getFieldOperators().containsKey(filterWrapper.getField()) &&
                filterContext.getFieldOperators().get(filterWrapper.getField()).contains(filterWrapper.getOperator());
    }

    private Predicate createPredicate(
            Root<T> root,
            CriteriaBuilder builder,
            BindingResult bindingResult,
            FilterWrapper filterWrapper
    ) {
        Path<T> path = getPath(root, filterWrapper, bindingResult);
        throwServerSideExceptionIfInvalid(bindingResult);

        Class<? extends Comparable<?>> dataType = getFieldDataType(path);
        AbstractFilterField<?> filterClass = getFieldFilter(dataType);
        FilterOperator filterOperator = FilterOperatorRegistry.getOperator(filterWrapper.getOperator());

        filterValidator.validateFilterFieldAndOperator(
                filterClass,
                filterOperator,
                filterWrapper,
                new ErrorWrapper(bindingResult, filterWrapper)
        );
        throwServerSideExceptionIfInvalid(bindingResult);

        List<?> values = filterWrapper.getValues()
                .stream()
                .map(value -> filterClass.cast(value, new ErrorWrapper(bindingResult, filterWrapper)))
                .toList();
        return filterOperator.apply(path, builder, values, new ErrorWrapper(bindingResult, filterWrapper));
    }

    private Path<T> getPath(Root<T> root, FilterWrapper filterWrapper, BindingResult bindingResult) {
        return filterPathGenerator.generate(root, filterWrapper.getField(), new ErrorWrapper(bindingResult, filterWrapper));
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Comparable<?>> getFieldDataType(Path<T> path) {
        return (Class<? extends Comparable<?>>) path.getJavaType();
    }

    private AbstractFilterField<?> getFieldFilter(Class<? extends Comparable<?>> dataType) {
        return FilterRegistry.getFieldFilter(dataType);
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
                                "buildFilterPredicate",
                                Root.class,
                                CriteriaQuery.class,
                                CriteriaBuilder.class,
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
                                "buildFilterPredicate",
                                Root.class,
                                CriteriaQuery.class,
                                CriteriaBuilder.class,
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
