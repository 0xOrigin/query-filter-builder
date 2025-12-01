package io.github._0xorigin.queryfilterbuilder.base.functions;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;

/**
 * A functional interface for providing a custom JPA {@link Expression}.
 * This is used when a filter or sort needs to be applied to a value that is not a direct field access,
 * such as a calculated value, a function call, or a complex join path.
 *
 * @param <T> The type of the root entity.
 * @param <K> The data type of the resulting expression.
 */
@FunctionalInterface
public interface ExpressionProviderFunction<T, K extends Comparable<? super K> & Serializable> {

    /**
     * Creates and returns a JPA {@link Expression}.
     *
     * @param root            The root of the query, used to navigate the entity model.
     * @param criteriaQuery   The criteria query being built.
     * @param criteriaBuilder The builder for constructing query elements.
     * @return The custom {@link Expression} to be used as the target for a filter or sort operation.
     */
    Expression<? extends K> apply(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder);

}
