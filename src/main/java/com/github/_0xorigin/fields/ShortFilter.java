package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractNumberFilterField;
import com.github._0xorigin.base.ErrorWrapper;

public class ShortFilter extends AbstractNumberFilterField<Short> {

    @Override
    public Short cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Short.parseShort(value.toString());
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
