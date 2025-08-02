package io.github._0xorigin.queryfilterbuilder.base.filterfield;

import io.github._0xorigin.queryfilterbuilder.base.wrappers.FilterErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;

import java.io.Serializable;

@FunctionalInterface
public interface FieldCaster<T extends Comparable<? super T> & Serializable> {

    T cast(String value);

    default T safeCast(String value, FilterErrorWrapper filterErrorWrapper) {
        try {
            return cast(value);
        } catch (RuntimeException exception) {
            FilterUtils.addError(
                filterErrorWrapper.bindingResult(),
                FilterUtils.generateFieldError(
                    filterErrorWrapper.bindingResult(),
                    filterErrorWrapper.filterWrapper().originalFieldName(),
                    value,
                    exception.getLocalizedMessage()
                )
            );
            return null;
        }
    }
}
