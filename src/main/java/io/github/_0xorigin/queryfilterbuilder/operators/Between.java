package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.Operator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Between extends AbstractFilterOperator {

    @Override
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, ErrorWrapper errorWrapper) {
        if (FilterUtils.isNotValidList(values))
            return Optional.empty();

        if (values.size() != 2) {
            FilterUtils.addError(
                errorWrapper,
                FilterUtils.generateFieldError(
                    errorWrapper,
                    values.toString(),
                    "Value must be a List with exactly 2 elements for " + Operator.BETWEEN.getValue() + " operator."
                )
            );

            return Optional.empty();
        }

        return Optional.ofNullable(cb.between(expression, values.get(0), values.get(values.size() - 1)));
    }
}
