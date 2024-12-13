package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.Operator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.time.format.DateTimeParseException;
import java.util.List;

public class Between extends AbstractFilterOperator {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values) || isEmpty(values))
            return cb.conjunction();

        if (values.size() != 2) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    values.toString(),
                    "Value must be a List with exactly 2 elements for " + Operator.BETWEEN.getValue() + " operator."
                )
            );

            return null;
        }

        try {
            if (isTemporalFilter(path)) {
                TemporalGroup group = getTemporalGroup(path);
                List<? extends Comparable> jdbcTypes = getJdbcTypes(path, group, values);
                Expression expression = getTemporalPath(group).apply(path);
                return cb.between(expression, jdbcTypes.get(0), jdbcTypes.get(jdbcTypes.size() - 1));
            }
        } catch (IllegalArgumentException | DateTimeParseException | ClassCastException e) {
            addError(errorWrapper, generateFieldError(errorWrapper, values.toString(), e.getMessage()));
            return null;
        }

        return cb.between(path.as(Comparable.class), (Comparable) values.get(0), (Comparable) values.get(values.size() - 1));
    }

}
