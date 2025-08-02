package io.github._0xorigin.queryfilterbuilder;

import org.springframework.data.jpa.domain.Specification;

public interface QueryFilterBuilder<T> {

    Specification<T> buildFilterSpecification(FilterContext<T> filterContext);

    Specification<T> buildSortSpecification(SortContext<T> sortContext);

}
