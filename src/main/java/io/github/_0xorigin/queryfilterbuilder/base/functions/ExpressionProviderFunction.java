package io.github._0xorigin.queryfilterbuilder.base.functions;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;

@FunctionalInterface
public interface ExpressionProviderFunction<T, K extends Comparable<? super K> & Serializable> {

    Expression<? extends K> apply(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);

}
