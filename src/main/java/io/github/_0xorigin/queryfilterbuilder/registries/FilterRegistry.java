package io.github._0xorigin.queryfilterbuilder.registries;

import io.github._0xorigin.queryfilterbuilder.base.filterfield.AbstractFilterField;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FilterRegistry {

    private final Map<Class<? extends Comparable<?>>, AbstractFilterField<?>> fieldConfigMap = new HashMap<>();

    public <K extends Comparable<? super K> & Serializable> FilterRegistry(List<AbstractFilterField<K>> filters) {
        filters.forEach(filter -> addFieldFilter(filter.getDataType(), filter));
    }

    private <K extends Comparable<? super K> & Serializable> void addFieldFilter(Class<K> dataType, AbstractFilterField<K> filterClass) {
        fieldConfigMap.put(dataType, filterClass);
    }

    public <K extends Comparable<? super K> & Serializable> AbstractFilterField<?> getFieldFilter(Class<K> dataType) {
        return fieldConfigMap.get(dataType);
    }
}
