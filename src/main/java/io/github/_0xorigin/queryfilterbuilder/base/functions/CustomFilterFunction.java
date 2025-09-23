package io.github._0xorigin.queryfilterbuilder.base.functions;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

/**
 * A functional interface for defining custom filter logic.
 * Implementations of this interface can create complex JPA {@link Predicate}s
 * based on the provided query context and input values.
 *
 * @param <T> The type of the root entity.
 * @see QuintFunction#apply(Object, Object, Object, Object, Object)
 */
@FunctionalInterface
public interface CustomFilterFunction<T> extends QuintFunction<Root<T>, CriteriaQuery<?>, CriteriaBuilder, List<?>, FilterErrorWrapper, Optional<Predicate>> {

}
