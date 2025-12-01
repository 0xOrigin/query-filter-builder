package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.FilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * A {@link FilterOperator} implementation that handles the 'isNotNull' operation.
 * This operator checks if an expression's value is not null.
 */
public final class IsNotNull implements FilterOperator {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation requires the {@code values} list to contain at least one non-null boolean-like element.
     * If the first value parses to {@code false}, this operator will generate an {@code isNull} predicate instead.
     * Otherwise, it generates an {@code isNotNull} predicate.
     */
    @Override
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, FilterErrorWrapper filterErrorWrapper) {
        if (FilterUtils.isNotValidList(values))
            return Optional.empty();

        if (!Boolean.parseBoolean(values.get(0).toString()))
            return Optional.ofNullable(cb.isNull(expression));

        return Optional.ofNullable(cb.isNotNull(expression));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Operator getOperatorConstant() {
        return Operator.IS_NOT_NULL;
    }
}
