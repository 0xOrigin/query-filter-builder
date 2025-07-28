package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;

import java.io.Serializable;

@FunctionalInterface
public interface FieldCaster<T extends Comparable<? super T> & Serializable> {

    T cast(Object value);

    default T safeCast(Object value, ErrorWrapper errorWrapper) {
        try {
            return cast(value);
        } catch (RuntimeException exception) {
            FilterUtils.addError(
                errorWrapper,
                FilterUtils.generateFieldError(
                    errorWrapper,
                    value.toString(),
                    exception.getLocalizedMessage()
                )
            );
            return null;
        }
    }
}
