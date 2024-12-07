package io.github._0xorigin.base;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

@FunctionalInterface
public interface CustomFilterFunction<T> extends QuintFunction<Root<T>, CriteriaQuery<?>, CriteriaBuilder, List<?>, ErrorWrapper, Predicate> {

}
