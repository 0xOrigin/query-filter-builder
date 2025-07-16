package io.github._0xorigin.queryfilterbuilder.operators;

import io.github._0xorigin.queryfilterbuilder.base.AbstractFilterOperator;
import io.github._0xorigin.queryfilterbuilder.base.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.FilterUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class EndsWith extends AbstractFilterOperator {

    @Override
    public <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> path, CriteriaBuilder cb, List<T> values, ErrorWrapper errorWrapper) {
        if (FilterUtils.isNotValidList(values))
            return Optional.empty();

        return Optional.ofNullable(cb.like(path.as(String.class), "%" + values.get(0)));
    }
}
