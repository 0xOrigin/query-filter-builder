package io.github._0xorigin.queryfilterbuilder.base.functions;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

import java.util.Optional;

@FunctionalInterface
public interface CustomSortFunction<T> extends QuadFunction<Root<T>, CriteriaQuery<?>, CriteriaBuilder, SortErrorWrapper, Optional<Order>> {

}
