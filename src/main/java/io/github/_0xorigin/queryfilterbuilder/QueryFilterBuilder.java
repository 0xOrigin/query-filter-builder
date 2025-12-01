package io.github._0xorigin.queryfilterbuilder;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public interface QueryFilterBuilder<T> {

    /**
     * Builds a JPA Specification for filtering based on the provided FilterContext.
     *
     * @param filterContext The context containing the filtering criteria. Must not be null.
     * @return A JPA Specification representing the filter.
     * @throws NullPointerException if the filterContext is null.
     */
    Specification<T> buildFilterSpecification(@NonNull FilterContext<T> filterContext);

    /**
     * Builds a JPA Specification for sorting based on the provided SortContext.
     *
     * @param sortContext The context containing the sorting criteria. Must not be null.
     * @return A JPA Specification representing the sort order.
     * @throws NullPointerException if the sortContext is null.
     */
    Specification<T> buildSortSpecification(@NonNull SortContext<T> sortContext);

}
