package io.github._0xorigin.registries;

import io.github._0xorigin.base.AbstractFilterField;
import io.github._0xorigin.fields.*;

import java.time.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FilterRegistry {

    private static final Map<Class<? extends Comparable<?>>, AbstractFilterField<?>> fieldConfigMap = new ConcurrentHashMap<>();

    static {
        addFieldFilter(Boolean.class, new BooleanFilter());
        addFieldFilter(Byte.class, new ByteFilter());
        addFieldFilter(Short.class, new ShortFilter());
        addFieldFilter(Integer.class, new IntegerFilter());
        addFieldFilter(Long.class, new LongFilter());
        addFieldFilter(Float.class, new FloatFilter());
        addFieldFilter(Double.class, new DoubleFilter());
        addFieldFilter(Character.class, new CharacterFilter());
        addFieldFilter(String.class, new StringFilter());
        addFieldFilter(UUID.class, new UuidFilter());
        addFieldFilter(Instant.class, new InstantFilter());
        addFieldFilter(OffsetDateTime.class, new OffsetDateTimeFilter());
        addFieldFilter(ZonedDateTime.class, new ZonedDateTimeFilter());
        addFieldFilter(OffsetTime.class, new OffsetTimeFilter());
        addFieldFilter(LocalDateTime.class, new LocalDateTimeFilter());
        addFieldFilter(LocalDate.class, new LocalDateFilter());
        addFieldFilter(LocalTime.class, new LocalTimeFilter());
        addFieldFilter(YearMonth.class, new YearMonthFilter());
        addFieldFilter(Year.class, new YearFilter());
    }

    public static synchronized void addFieldFilter(Class<? extends Comparable<?>> dataType, AbstractFilterField<?> filterClass) {
        // This method is intentionally public in case if you want to add custom filters
        fieldConfigMap.put(dataType, filterClass);
    }

    public static synchronized AbstractFilterField<?> getFieldFilter(Class<? extends Comparable<?>> dataType) {
        return fieldConfigMap.get(dataType);
    }

    public static synchronized Map<Class<? extends Comparable<?>>, AbstractFilterField<?>> getAllFieldConfigs() {
        return fieldConfigMap;
    }

}
