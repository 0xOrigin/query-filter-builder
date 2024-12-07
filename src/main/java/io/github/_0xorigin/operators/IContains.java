package io.github._0xorigin.operators;

import io.github._0xorigin.base.AbstractFilterOperator;
import io.github._0xorigin.base.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class IContains extends AbstractFilterOperator {

    @Override
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values))
            return cb.conjunction();

        return cb.like(cb.upper(path.as(String.class)), "%" + ((String) values.get(0)).toUpperCase() + "%");
    }

}
