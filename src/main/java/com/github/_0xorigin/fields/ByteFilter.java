package com.github._0xorigin.fields;

import com.github._0xorigin.base.AbstractNumberFilterField;
import com.github._0xorigin.base.ErrorWrapper;

public class ByteFilter extends AbstractNumberFilterField<Byte> {

    @Override
    public Byte cast(Object value, ErrorWrapper errorWrapper) {
        try {
            return Byte.parseByte(value.toString());
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
