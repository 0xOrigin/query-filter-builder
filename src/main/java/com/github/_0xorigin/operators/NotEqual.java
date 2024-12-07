package com.github._0xorigin.operators;

import com.github._0xorigin.base.AbstractFilterOperator;
import com.github._0xorigin.base.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.util.List;

public class NotEqual extends AbstractFilterOperator {

    @Override
    public Predicate apply(Path<?> path, CriteriaBuilder cb, List<?> values, ErrorWrapper errorWrapper) {
        if (isContainNulls(values))
            return cb.conjunction();

        return cb.notEqual(path, values.get(0));
    }

}