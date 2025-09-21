package io.github._0xorigin.queryfilterbuilder.base.holders;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * A holder for the configuration of a standard filter.
 *
 * @param operators                  The set of allowed {@link Operator}s for this filter.
 * @param sourceTypes                The set of allowed {@link SourceType}s for this filter.
 * @param expressionProviderFunction An optional function to provide a custom JPA {@link Expression} for this filter.
 * @param <T>                        The type of the root entity.
 * @param <K>                        The data type of the field or expression.
 */
public record FilterHolder<T, K extends Comparable<? super K> & Serializable> (
    Set<Operator> operators,
    Set<SourceType> sourceTypes,
    Optional<ExpressionProviderFunction<T, K>> expressionProviderFunction
) {
    /**
     * Gets the custom JPA {@link Expression} if an {@link ExpressionProviderFunction} is present.
     *
     * @param root            The root of the query.
     * @param criteriaQuery   The criteria query being built.
     * @param criteriaBuilder The builder for constructing query elements.
     * @param <Y>             The data type of the expression.
     * @return An {@link Optional} containing the custom {@link Expression}, or empty if no provider function is defined.
     */
    @SuppressWarnings("unchecked")
    public <Y extends Comparable<? super Y> & Serializable> Optional<Expression<Y>> getExpression(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return expressionProviderFunction.map(providerFunction -> (Expression<Y>) providerFunction.apply(root, criteriaQuery, criteriaBuilder));
    }
}
