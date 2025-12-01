package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.builders.FilterBuilder;
import io.github._0xorigin.queryfilterbuilder.base.builders.SortBuilder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.*;

/**
 * Implementation of the QueryFilterBuilder interface, responsible for building JPA Specifications for filtering and sorting.
 *
 * @param <T> The type of the entity being filtered and sorted.
 */
public final class QueryFilterBuilderImp<T> implements QueryFilterBuilder<T> {

    private final FilterBuilder<T> filterBuilder;
    private final SortBuilder<T> sortBuilder;
    private final Logger log = LoggerFactory.getLogger(QueryFilterBuilderImp.class);

    /**
     * Constructs a new QueryFilterBuilderImp with the specified filter and sort builders.
     *
     * @param filterBuilder The builder responsible for creating filter predicates. Must not be null.
     * @param sortBuilder The builder responsible for creating sort orders. Must not be null.
     */
    public QueryFilterBuilderImp(
        final FilterBuilder<T> filterBuilder,
        final SortBuilder<T> sortBuilder
    ) {
        this.filterBuilder = filterBuilder;
        this.sortBuilder = sortBuilder;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation builds a specification that combines multiple filter criteria using an AND operator.
     * It processes each filter defined in the {@link FilterContext}, generating a {@link Predicate} for each.
     * If the provided context is null, it throws a {@link NullPointerException}.
     * If any of the filter criteria are invalid, a client-side exception is thrown.
     * If no predicates are generated, the specification will not filter any results.
     */
    @Override
    public Specification<T> buildFilterSpecification(@NonNull final FilterContext<T> filterContext) {
        Objects.requireNonNull(filterContext, "FilterContext must not be null");
        final ErrorHolder errorHolder = new ErrorHolder(getBindingResult(), getFilterMethodParameter());
        return (root, criteriaQuery, criteriaBuilder) -> {
            final List<Predicate> predicates = filterBuilder.getDistinctFilterWrappers(filterContext).stream()
                    .map(filterWrapper -> filterBuilder.buildPredicateForWrapper(root, criteriaQuery, criteriaBuilder, filterContext, filterWrapper, errorHolder))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
//            log.debug("Predicates: {}", predicates.size());
            FilterUtils.throwClientSideExceptionIfInvalid(errorHolder);
            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation builds a specification that applies sorting to the query.
     * It processes each sort criterion defined in the {@link SortContext}, generating an {@link Order} for each.
     * If the provided context is null, it throws a {@link NullPointerException}.
     * If any of the sort criteria are invalid, a client-side exception is thrown.
     * The generated orders are applied to the query.
     */
    @Override
    public Specification<T> buildSortSpecification(@NonNull final SortContext<T> sortContext) {
        Objects.requireNonNull(sortContext, "SortContext must not be null");
        final ErrorHolder errorHolder = new ErrorHolder(getBindingResult(), getSortMethodParameter());
        return (root, criteriaQuery, criteriaBuilder) -> {
            final List<Order> orders = sortBuilder.getDistinctSortWrappers(sortContext).stream()
                    .map(sortWrapper -> sortBuilder.buildOrderForWrapper(root, criteriaQuery, criteriaBuilder, sortContext, sortWrapper, errorHolder))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
//            log.debug("Orders: {}", orders.size());
            FilterUtils.throwClientSideExceptionIfInvalid(errorHolder);
            criteriaQuery.orderBy(orders);
            return null;
        };
    }

    private BindingResult getBindingResult() {
        return new BeanPropertyBindingResult(this, "queryFilterBuilder");
    }

    private MethodParameter getFilterMethodParameter() {
        try {
            return new MethodParameter(
                this.getClass()
                    .getMethod(
                        "buildFilterSpecification",
                        FilterContext.class
                    ),
            0
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private MethodParameter getSortMethodParameter() {
        try {
            return new MethodParameter(
                this.getClass()
                    .getMethod(
                        "buildSortSpecification",
                        SortContext.class
                    ),
            0
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
