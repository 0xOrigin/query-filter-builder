package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.Operator;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class Between extends AbstractFilterOperator {

    @Override
//    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values) || isEmpty(values))
            return Optional.empty();

        if (values.size() != 2) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    values.toString(),
                    "Value must be a List with exactly 2 elements for " + Operator.BETWEEN.getValue() + " operator."
                )
            );

            return Optional.empty();
        }

//        try {
//            if (isTemporalFilter(path)) {
//                TemporalGroup group = getTemporalGroup(path);
//                List<? extends Comparable> jdbcTypes = getJdbcTypes(path, group, values);
//                Expression expression = getTemporalPath(group).apply(path);
//                return cb.between(expression, jdbcTypes.get(0), jdbcTypes.get(jdbcTypes.size() - 1));
//            }
//        } catch (IllegalArgumentException | DateTimeParseException | ClassCastException e) {
//            addError(errorWrapper, generateFieldError(errorWrapper, values.toString(), e.getMessage()));
//            return null;
//        }

        return Optional.ofNullable(cb.between(expression, values.get(0), values.get(values.size() - 1)));
    }

}
