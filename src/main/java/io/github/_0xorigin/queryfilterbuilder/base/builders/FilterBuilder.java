package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.Optional;

public interface FilterBuilder<T> {

    Collection<FilterWrapper> getDistinctFilterWrappers(FilterContext<T> filterContext);

    Optional<Predicate> buildPredicateForWrapper(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, FilterContext<T> filterContext, FilterWrapper filterWrapper, ErrorHolder errorHolder);

}
