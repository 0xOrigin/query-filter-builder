package io.github._0xorigin.queryfilterbuilder.base;

import io.github._0xorigin.queryfilterbuilder.FilterContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.jpa.domain.Specification;

@FunctionalInterface
public interface QueryFilterBuilder<T> {

    Specification<T> buildFilterSpecification(HttpServletRequest request, FilterContext<T> filterContext);

}
