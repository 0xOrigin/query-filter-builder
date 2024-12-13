package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.format.DateTimeParseException;
import java.util.List;

public class GreaterThan extends AbstractFilterOperator {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values) || isEmpty(values))
            return cb.conjunction();

        try {
            if (isTemporalFilter(path)) {
                TemporalGroup group = getTemporalGroup(path);
                List<? extends Comparable> jdbcTypes = getJdbcTypes(path, group, List.of(values.get(0)));
                Expression expression = getTemporalPath(group).apply(path);
                return cb.greaterThan(expression, jdbcTypes.get(0));
            }
        } catch (IllegalArgumentException | DateTimeParseException | ClassCastException e) {
            addError(errorWrapper, generateFieldError(errorWrapper, values.toString(), e.getMessage()));
            return null;
        }

        return cb.greaterThan(path.as(Comparable.class), (Comparable) values.get(0));
    }

}
