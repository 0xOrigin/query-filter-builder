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
 * A {@link FilterOperator} implementation that handles the 'iendsWith' operation (case-insensitive ends with).
 * This operator checks if a string expression ends with a specified substring, ignoring case.
 */
public final class IEndsWith implements FilterOperator {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation requires the {@code values} list to contain at least one non-null element.
     * It uses the first element in the list as the substring to search for.
     * Both the expression and the value are converted to uppercase for the comparison.
     */
    @Override
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, FilterErrorWrapper filterErrorWrapper) {
        if (FilterUtils.isNotValidList(values))
            return Optional.empty();

        return Optional.ofNullable(cb.like(cb.upper(expression.as(String.class)),"%" + values.get(0).toString().toUpperCase()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Operator getOperatorConstant() {
        return Operator.IENDS_WITH;
    }
}
