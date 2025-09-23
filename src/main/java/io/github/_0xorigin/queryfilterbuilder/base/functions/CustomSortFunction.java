package io.github._0xorigin.queryfilterbuilder.base.functions;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

import java.util.Optional;

/**
 * A functional interface for defining custom sort logic.
 * Implementations of this interface can create complex JPA {@link Order} objects
 * based on the provided query context.
 *
 * @param <T> The type of the root entity.
 * @see QuadFunction#apply(Object, Object, Object, Object)
 */
@FunctionalInterface
public interface CustomSortFunction<T> extends QuadFunction<Root<T>, CriteriaQuery<?>, CriteriaBuilder, SortErrorWrapper, Optional<Order>> {

}
