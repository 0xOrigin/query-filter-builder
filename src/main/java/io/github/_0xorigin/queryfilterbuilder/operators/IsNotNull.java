package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class IsNotNull extends AbstractFilterOperator {

    @Override
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values) || isEmpty(values))
            return cb.conjunction();

        if (!(Boolean) values.get(0))
            return cb.isNull(path);

        return cb.isNotNull(path);
    }

}