package io.github._0xorigin.queryfilterbuilder.base.generators;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import org.springframework.validation.BindingResult;

import java.io.Serializable;

/**
 * A functional interface for generating a JPA {@link Expression} from a string field path.
 * Implementations are responsible for parsing delimited paths (e.g., "customer.address.city")
 * and creating the appropriate joins to resolve the final path.
 *
 * @param <T> The type of the root entity.
 */
@FunctionalInterface
public interface PathGenerator<T> {

    /**
     * Generates a JPA {@link Expression} for the given field path.
     *
     * @param root              The root of the query, from which to start path navigation.
     * @param field             The delimited field path (e.g., "customer.name").
     * @param originalFieldName The original field name from the request, used for error reporting.
     * @param bindingResult     The binding result to which any path resolution errors will be added.
     * @param <K>               The data type of the target field.
     * @return The generated {@link Expression} corresponding to the field path.
     */
    <K extends Comparable<? super K> & Serializable> Expression<K> generate(Root<T> root, String field, String originalFieldName, BindingResult bindingResult);

}
