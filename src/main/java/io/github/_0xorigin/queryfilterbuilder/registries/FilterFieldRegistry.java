package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FilterFieldRegistry {

    private final Map<Class<? extends Comparable<?>>, AbstractFilterField<? extends Comparable<?>>> filterFields = new HashMap<>();

    public FilterFieldRegistry(List<AbstractFilterField<? extends Comparable<?>>> filterFields) {
        filterFields.forEach(filterField -> addFilterField(filterField.getDataType(), filterField));
    }

    private <K extends Comparable<? super K> & Serializable> void addFilterField(Class<? extends Comparable<?>> dataType, AbstractFilterField<K> filterField) {
        filterFields.put(dataType, filterField);
    }

    public <K extends Comparable<? super K> & Serializable> AbstractFilterField<? extends Comparable<?>> getFilterField(Class<K> dataType) {
        return filterFields.get(dataType);
    }

    public Map<Class<? extends Comparable<?>>, AbstractFilterField<? extends Comparable<?>>> getFilterFields() {
        return Collections.unmodifiableMap(filterFields);
    }
}
