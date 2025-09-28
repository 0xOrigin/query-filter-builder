package io.github._0xorigin.queryfilterbuilder.base.enumfield;

/**
 * A concrete implementation of the {@link AbstractEnumFilterField} abstraction.
 * This class provides a default implementation for the {@link #cast(Class, String)} method,
 * which uses the {@link Enum#valueOf(Class, String)} method to cast a string value to an enum type.
 */
public final class EnumFilterFieldImp extends AbstractEnumFilterField {

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Enum<T>> T cast(Class<T> enumClass, String value) {
        return Enum.valueOf(enumClass, value);
    }
}
