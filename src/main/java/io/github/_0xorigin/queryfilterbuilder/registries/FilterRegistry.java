package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FilterRegistry {

    private final Map<Class<? extends Comparable<?>>, AbstractFilterField<? extends Comparable<?>>> fieldConfigMap = new HashMap<>();

    public FilterRegistry(List<AbstractFilterField<? extends Comparable<?>>> filters) {
        filters.forEach(filter -> addFieldFilter(filter.getDataType(), filter));
    }

    private <K extends Comparable<? super K> & Serializable> void addFieldFilter(Class<? extends Comparable<?>> dataType, AbstractFilterField<K> filterClass) {
        fieldConfigMap.put(dataType, filterClass);
    }

    public <K extends Comparable<? super K> & Serializable> AbstractFilterField<? extends Comparable<?>> getFieldFilter(Class<K> dataType) {
        return fieldConfigMap.get(dataType);
    }
}
