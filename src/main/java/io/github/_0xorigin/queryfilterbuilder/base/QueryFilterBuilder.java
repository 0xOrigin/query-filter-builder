package io.github._0xorigin.queryfilterbuilder.base;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import org.springframework.data.jpa.domain.Specification;

@FunctionalInterface
public interface QueryFilterBuilder<T> {

    Specification<T> buildFilterSpecification(FilterContext<T> filterContext);

}
