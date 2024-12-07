package com.github._0xorigin.operators;

import com.github._0xorigin.base.AbstractFilterOperator;
import com.github._0xorigin.base.ErrorWrapper;
import com.github._0xorigin.base.Operator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class NotBetween extends AbstractFilterOperator {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values))
            return cb.conjunction();

        if (values.size() != 2) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    values.toString(),
                    "Value must be a List with exactly 2 elements for " + Operator.NOT_BETWEEN.getValue() + " operator."
                )
            );

            return null;
        }

        if (isTemporalFilter(path)) {
            TemporalGroup group = getTemporalGroup(path);
            List<? extends Comparable> jdbcTypes = getJdbcTypes(path, group, values);
            Expression expression = getTemporalPath(group).apply(path);
            return cb.not(cb.between(expression, jdbcTypes.get(0), jdbcTypes.get(jdbcTypes.size() - 1)));
        }

        return cb.not(cb.between(path.as(Comparable.class), (Comparable) values.get(0), (Comparable) values.get(values.size() - 1)));
    }

}
