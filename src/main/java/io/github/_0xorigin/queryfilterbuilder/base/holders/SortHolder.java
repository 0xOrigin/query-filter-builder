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

public record SortHolder<T, K extends Comparable<? super K> & Serializable> (
    Set<Sort.Direction> directions,
    Set<SourceType> sourceTypes,
    Optional<ExpressionProviderFunction<T, K>> expressionProviderFunction
) {
    @SuppressWarnings("unchecked")
    public <Y extends Comparable<? super Y> & Serializable> Optional<Expression<Y>> getExpression(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return expressionProviderFunction.map(providerFunction -> (Expression<Y>) providerFunction.apply(root, criteriaQuery, criteriaBuilder));
    }
}
