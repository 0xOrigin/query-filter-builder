package io.github._0xorigin.queryfilterbuilder.base;

import java.io.Serializable;

@FunctionalInterface
public interface FieldCaster<T extends Comparable<? super T> & Serializable> {

    T cast(Object value, ErrorWrapper errorWrapper);

    default T safeCast(Object value, ErrorWrapper errorWrapper) {
        try {
            return cast(value, errorWrapper);
        } catch (RuntimeException exception) {
            FilterUtils.addError(
                errorWrapper,
                FilterUtils.generateFieldError(
                    errorWrapper,
                    value.toString(),
                    exception.getLocalizedMessage()
                )
            );
        }
        return null;
    }
}
