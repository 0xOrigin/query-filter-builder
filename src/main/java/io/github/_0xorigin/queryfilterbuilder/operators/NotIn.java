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
 * A {@link FilterOperator} implementation that handles the 'notIn' operation.
 * This operator checks if an expression's value is not present in a list of specified values.
 */
public final class NotIn implements FilterOperator {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation requires the {@code values} list to be non-empty and contain no nulls.
     */
    @Override
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, FilterErrorWrapper filterErrorWrapper) {
        if (FilterUtils.isNotValidList(values))
            return Optional.empty();

        return Optional.ofNullable(cb.not(expression.in(values)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Operator getOperatorConstant() {
        return Operator.NOT_IN;
    }
}
