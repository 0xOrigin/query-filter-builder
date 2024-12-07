package com.github._0xorigin.operators;

import com.github._0xorigin.base.AbstractFilterOperator;
import com.github._0xorigin.base.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class GreaterThanEqual extends AbstractFilterOperator {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values))
            return cb.conjunction();

        if (isTemporalFilter(path)) {
            TemporalGroup group = getTemporalGroup(path);
            List<? extends Comparable> jdbcTypes = getJdbcTypes(path, group, List.of(values.get(0)));
            Expression expression = getTemporalPath(group).apply(path);
            return cb.greaterThanOrEqualTo(expression, jdbcTypes.get(0));
        }

        return cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) values.get(0));
    }

}
