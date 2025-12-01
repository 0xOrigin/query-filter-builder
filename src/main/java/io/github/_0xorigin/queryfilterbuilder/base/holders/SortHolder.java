package io.github._0xorigin.queryfilterbuilder.base.holders;

import io.github._0xorigin.queryfilterbuilder.base.enums.SourceType;
import io.github._0xorigin.queryfilterbuilder.base.functions.ExpressionProviderFunction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * A holder for the configuration of a standard sort.
 *
 * @param directions                 The set of allowed {@link Sort.Direction}s for this sort.
 * @param sourceTypes                The set of allowed {@link SourceType}s for this sort.
 * @param expressionProviderFunction An optional function to provide a custom JPA {@link Expression} for this sort.
 * @param <T>                        The type of the root entity.
 * @param <K>                        The data type of the field or expression.
 */
public record SortHolder<T, K extends Comparable<? super K> & Serializable> (
    Set<Sort.Direction> directions,
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
