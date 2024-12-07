package io.github._0xorigin.base;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractFilterOperator extends FilterUtils implements FilterOperator {

    protected enum TemporalGroup {
        Date, Time, Timestamp;
    }

    private static final Map<Class<? extends Temporal>, TemporalGroup> temporalGroup = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Temporal>, Function<List<?>, ?>> temporalConverter = new ConcurrentHashMap<>();
    private static final Map<TemporalGroup, Function<Path<?>, Expression<?>>> temporalPath = new ConcurrentHashMap<>();
    private static final Map<TemporalGroup, Function<List<?>, List<? extends Comparable<?>>>> temporalToJdbcType = new ConcurrentHashMap<>();

    static {
        addTemporalGroup(Instant.class, TemporalGroup.Timestamp);
        addTemporalGroup(OffsetDateTime.class, TemporalGroup.Timestamp);
        addTemporalGroup(ZonedDateTime.class, TemporalGroup.Timestamp);
        addTemporalGroup(LocalDateTime.class, TemporalGroup.Timestamp);
        addTemporalGroup(LocalDate.class, TemporalGroup.Date);
        addTemporalGroup(YearMonth.class, TemporalGroup.Date);
        addTemporalGroup(Year.class, TemporalGroup.Date);
        addTemporalGroup(LocalTime.class, TemporalGroup.Time);
        addTemporalGroup(OffsetTime.class, TemporalGroup.Time);

        addTemporalPath(TemporalGroup.Date, path -> path.as(Date.class));
        addTemporalPath(TemporalGroup.Time, path -> path.as(Time.class));
        addTemporalPath(TemporalGroup.Timestamp, path -> path.as(Timestamp.class));

        addTemporalConverter(Instant.class, generateTemporalConverter(null));
        addTemporalConverter(OffsetDateTime.class, generateTemporalConverter(o -> ((OffsetDateTime) o).toInstant()));
        addTemporalConverter(ZonedDateTime.class, generateTemporalConverter(o -> ((ZonedDateTime) o).toInstant()));
        addTemporalConverter(LocalDateTime.class, generateTemporalConverter(o -> Instant.parse(((LocalDateTime) o).toString())));
        addTemporalConverter(LocalDate.class, generateTemporalConverter(o -> ((LocalDate) o).toString()));
        addTemporalConverter(YearMonth.class, generateTemporalConverter(o -> ((YearMonth) o).toString()));
        addTemporalConverter(Year.class, generateTemporalConverter(o -> ((Year) o).toString()));
        addTemporalConverter(LocalTime.class, generateTemporalConverter(o -> ((LocalTime) o).toString()));
        addTemporalConverter(OffsetTime.class, generateTemporalConverter(o -> ((OffsetTime) o).toString()));

        addTemporalToJdbcType(TemporalGroup.Date, generateTemporalToJdbcConverter(o -> Date.valueOf(o.toString())));
        addTemporalToJdbcType(TemporalGroup.Time, generateTemporalToJdbcConverter(o -> Time.valueOf(o.toString())));
        addTemporalToJdbcType(TemporalGroup.Timestamp, generateTemporalToJdbcConverter(o -> Timestamp.from((Instant) o)));
    }

    private static Function<List<?>, ?> generateTemporalConverter(Function<Object, ?> converter) {
        if (converter == null)
            return values -> values.stream().filter(Objects::nonNull).toList();
        return values -> values.stream().filter(Objects::nonNull).map(converter).toList();
    }

    @SuppressWarnings("unchecked")
    private static Function<List<?>, List<? extends Comparable<?>>> generateTemporalToJdbcConverter(Function<Object, ?> converter) {
        if (converter == null)
            return values -> (List<? extends Comparable<?>>) values.stream().filter(Objects::nonNull).toList();
        return values -> (List<? extends Comparable<?>>) values.stream().filter(Objects::nonNull).map(converter).toList();
    }

    private static void addTemporalGroup(Class<? extends Temporal> temporalClass, TemporalGroup group) {
        temporalGroup.put(temporalClass, group);
    }

    private static void addTemporalConverter(Class<? extends Temporal> temporalClass, Function<List<?>, ?> converter) {
        temporalConverter.put(temporalClass, converter);
    }

    private static void addTemporalPath(TemporalGroup group, Function<Path<?>, Expression<?>> path) {
        temporalPath.put(group, path);
    }

    private static void addTemporalToJdbcType(TemporalGroup group, Function<List<?>, List<? extends Comparable<?>>> converter) {
        temporalToJdbcType.put(group, converter);
    }

    protected TemporalGroup getTemporalGroup(Path<?> path) {
        return temporalGroup.get(path.getJavaType());
    }

    protected Function<Path<?>, Expression<?>> getTemporalPath(TemporalGroup group) {
        return temporalPath.get(group);
    }

    protected Function<List<?>, ?> getTemporalConverter(Path<?> path) {
        return temporalConverter.get(path.getJavaType());
    }

    protected Function<List<?>, List<? extends Comparable<?>>> getTemporalToJdbcTypes(TemporalGroup group) {
        return temporalToJdbcType.get(group);
    }

    protected List<? extends Comparable<?>> getJdbcTypes(Path<?> path, TemporalGroup group, List<?> values) {
        List<?> convertedTemporal = (List<?>) getTemporalConverter(path).apply(values);
        return getTemporalToJdbcTypes(group).apply(convertedTemporal);
    }

    protected Boolean isTemporalFilter(Path<?> path) {
        return Arrays.stream(path.getJavaType().getInterfaces()).anyMatch(i -> i == Temporal.class);
    }

    protected Boolean isContainNulls(List<?> values) {
        return values.stream().anyMatch(Objects::isNull);
    }

}
