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

    /**
     * Extracts and consolidates distinct sort requests from the given {@link SortContext}.
     * This method is responsible for parsing the source (e.g., HTTP request) and creating a collection of unique {@link SortWrapper} objects.
     *
     * @param sortContext The context containing the sort definitions and the source of sort requests. Must not be null.
     * @return A collection of distinct {@link SortWrapper}s representing the sorts to be applied.
     *         Returns an empty collection if no valid sorts are found.
     */
    Collection<SortWrapper> getDistinctSortWrappers(@NonNull SortContext<T> sortContext);

    /**
     * Builds a JPA {@link Order} for a given {@link SortWrapper}.
     * This method contains the core logic for converting a single sort request into a database query order clause.
     *
     * @param root            The root entity in the query. Used to build path expressions.
     * @param criteriaQuery   The criteria query being built.
     * @param criteriaBuilder The builder used to construct criteria query objects.
     * @param sortContext     The context containing the sort definitions.
     * @param sortWrapper     The specific sort wrapper for which to build the order.
     * @param errorHolder     An object to collect any validation or processing errors that occur.
     * @return An {@link Optional} containing the generated {@link Order} if the sort is valid and applicable.
     *         Returns an empty optional if the sort is invalid or an error occurs (which will be logged in the errorHolder).
     */
    Optional<Order> buildOrderForWrapper(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, SortContext<T> sortContext, SortWrapper sortWrapper, ErrorHolder errorHolder);

}
