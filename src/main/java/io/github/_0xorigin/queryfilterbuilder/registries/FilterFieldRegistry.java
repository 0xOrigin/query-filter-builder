package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A registry for all available {@link AbstractFilterField} implementations.
 * This class holds a map of data types to their corresponding filter field handlers, allowing for easy lookup.
 */
public final class FilterFieldRegistry {

    private final Map<Class<? extends Comparable<?>>, AbstractFilterField<? extends Comparable<?>>> filterFields = new HashMap<>();

    /**
     * Constructs a new registry and populates it with the provided list of filter fields.
     *
     * @param filterFields A list of {@code AbstractFilterField} beans, typically injected by Spring.
     */
    public FilterFieldRegistry(List<AbstractFilterField<? extends Comparable<?>>> filterFields) {
        filterFields.forEach(filterField -> addFilterField(filterField.getDataType(), filterField));
    }

    /**
     * Adds a filter field to the registry.
     *
     * @param dataType    The class of the data type (e.g., {@code String.class}, {@code Integer.class}).
     * @param filterField The filter field to add.
     * @param <K>         The data type.
     */
    private <K extends Comparable<? super K> & Serializable> void addFilterField(Class<? extends Comparable<?>> dataType, AbstractFilterField<K> filterField) {
        filterFields.put(dataType, filterField);
    }

    /**
     * Retrieves the {@link AbstractFilterField} implementation for a given data type.
     *
     * @param dataType The class of the data type (e.g., {@code String.class}, {@code Integer.class}).
     * @param <K>      The data type.
     * @return The corresponding {@code AbstractFilterField}, or {@code null} if no handler is registered for the type.
     */
    public <K extends Comparable<? super K> & Serializable> AbstractFilterField<? extends Comparable<?>> getFilterField(Class<K> dataType) {
        return filterFields.get(dataType);
    }

    /**
     * Gets an unmodifiable view of the registered filter fields.
     *
     * @return An unmodifiable map of data types to filter field handlers.
     */
    public Map<Class<? extends Comparable<?>>, AbstractFilterField<? extends Comparable<?>>> getFilterFields() {
        return Collections.unmodifiableMap(filterFields);
    }
}
