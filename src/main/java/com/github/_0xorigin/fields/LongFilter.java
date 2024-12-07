package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractNumberFilterField;
import com.github._0xorigin.base.ErrorWrapper;

public class LongFilter extends AbstractNumberFilterField<Long> {

    @Override
    public Long cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    value.toString(),
                    e.getLocalizedMessage()
                )
            );
        }

        return null;
    }

}
