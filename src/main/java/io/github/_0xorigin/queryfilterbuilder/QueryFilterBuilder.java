package io.github._0xorigin.queryfilterbuilder;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

public interface QueryFilterBuilder<T> {

    Specification<T> buildFilterSpecification(@NonNull FilterContext<T> filterContext);

    Specification<T> buildSortSpecification(@NonNull SortContext<T> sortContext);

}
