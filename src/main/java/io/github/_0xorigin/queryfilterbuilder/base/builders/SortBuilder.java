package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.SortContext;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Optional;

public interface SortBuilder<T> {

    Collection<SortWrapper> getDistinctSortWrappers(@NonNull SortContext<T> sortContext);

    Optional<Order> buildOrderForWrapper(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, SortContext<T> sortContext, SortWrapper sortWrapper, ErrorHolder errorHolder);

}
