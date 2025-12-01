package io.github._0xorigin.queryfilterbuilder.base.filteroperator;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for a specific filter operator implementation.
 * Each implementation is responsible for creating a JPA {@link Predicate}
 * for a given expression and set of values.
 */
public interface FilterOperator {

    /**
     * Applies the filter logic to the given expression and values.
     *
     * @param expression         The JPA expression representing the database field.
     * @param cb                 The {@link CriteriaBuilder} to use for creating the predicate.
     * @param values             The list of values to be used in the comparison. The number of values required depends on the operator.
     * @param filterErrorWrapper A wrapper for collecting errors that may occur during the operation.
     * @param <T>                The data type of the expression and values.
     * @return An {@link Optional} containing the created {@link Predicate}, or empty if the operation is not applicable or an error occurs.
     */
    <T extends Comparable<? super T> & Serializable> Optional<Predicate> apply(Expression<T> expression, CriteriaBuilder cb, List<T> values, FilterErrorWrapper filterErrorWrapper);

    /**
     * Gets the corresponding {@link Operator} enum constant for this implementation.
     *
     * @return The {@link Operator} constant.
     */
    Operator getOperatorConstant();
}
