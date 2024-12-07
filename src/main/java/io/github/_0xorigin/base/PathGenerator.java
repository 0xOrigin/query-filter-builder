package io.github._0xorigin.base;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public interface PathGenerator<T> {

    Path<T> generate(Root<T> root, String field, ErrorWrapper errorWrapper);

}
