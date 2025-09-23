package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Optional;

public interface FilterBuilder<T> {

    /**
     * Extracts and consolidates distinct filter requests from the given {@link FilterContext}.
     * This method is responsible for parsing the source (e.g., HTTP request) and creating a collection of unique {@link FilterWrapper} objects.
     *
     * @param filterContext The context containing the filter definitions and the source of filter requests. Must not be null.
     * @return A collection of distinct {@link FilterWrapper}s representing the filters to be applied.
     *         Returns an empty collection if no valid filters are found.
     */
    Collection<FilterWrapper> getDistinctFilterWrappers(@NonNull FilterContext<T> filterContext);

    /**
     * Builds a JPA {@link Predicate} for a given {@link FilterWrapper}.
     * This method contains the core logic for converting a single filter request into a database query condition.
     *
     * @param root            The root entity in the query. Used to build path expressions.
     * @param criteriaQuery   The criteria query being built.
     * @param criteriaBuilder The builder used to construct criteria query objects, such as predicates.
     * @param filterContext   The context containing the filter definitions.
     * @param filterWrapper   The specific filter wrapper for which to build the predicate.
     * @param errorHolder     An object to collect any validation or processing errors that occur.
     * @return An {@link Optional} containing the generated {@link Predicate} if the filter is valid and applicable.
     *         Returns an empty optional if the filter is invalid, results in no condition, or an error occurs (which will be logged in the errorHolder).
     */
    Optional<Predicate> buildPredicateForWrapper(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, FilterContext<T> filterContext, FilterWrapper filterWrapper, ErrorHolder errorHolder);

}
