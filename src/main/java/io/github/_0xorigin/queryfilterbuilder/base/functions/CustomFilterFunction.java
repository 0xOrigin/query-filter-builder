package io.github._0xorigin.queryfilterbuilder.base.functions;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.ErrorWrapper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface CustomFilterFunction<T> extends QuintFunction<Root<T>, CriteriaQuery<?>, CriteriaBuilder, List<?>, ErrorWrapper, Optional<Predicate>> {

}
