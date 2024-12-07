package io.github._0xorigin.base;

import io.github._0xorigin.FilterContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface QueryFilterBuilder<T> {

    Predicate buildFilterPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb, FilterContext<T> filterContext);

}
