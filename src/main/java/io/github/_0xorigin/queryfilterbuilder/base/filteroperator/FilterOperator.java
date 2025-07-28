package io.github._0xorigin.queryfilterbuilder.base.filteroperator;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface FilterOperator {

    <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, ErrorWrapper errorWrapper);

    Operator getOperatorConstant();
}
