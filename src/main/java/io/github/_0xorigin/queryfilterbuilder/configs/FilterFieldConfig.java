package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.filters.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for creating beans for all supported filter field types.
 * Each bean represents a specific data type and defines its supported operators and casting logic.
 */
@Configuration
public class FilterFieldConfig {

    /**
     * Creates a bean for filtering {@link java.math.BigDecimal} fields.
     * @return A {@link BigDecimalFilter} instance.
     */
    @Bean
    public BigDecimalFilter bigDecimalFilter() {
        return new BigDecimalFilter();
    }

    /**
     * Creates a bean for filtering {@link java.math.BigInteger} fields.
     * @return A {@link BigIntegerFilter} instance.
     */
    @Bean
    public BigIntegerFilter bigIntegerFilter() {
        return new BigIntegerFilter();
    }

    /**
     * Creates a bean for filtering {@link Boolean} fields.
     * @return A {@link BooleanFilter} instance.
     */
    @Bean
    public BooleanFilter booleanFilter() {
        return new BooleanFilter();
    }

    /**
     * Creates a bean for filtering {@link Byte} fields.
     * @return A {@link ByteFilter} instance.
     */
    @Bean
    public ByteFilter byteFilter() {
        return new ByteFilter();
    }

    /**
     * Creates a bean for filtering {@link Character} fields.
     * @return A {@link CharacterFilter} instance.
     */
    @Bean
    public CharacterFilter characterFilter() {
        return new CharacterFilter();
    }

    /**
     * Creates a bean for filtering {@link Double} fields.
     * @return A {@link DoubleFilter} instance.
     */
    @Bean
    public DoubleFilter doubleFilter() {
        return new DoubleFilter();
    }

    /**
     * Creates a bean for filtering {@link Float} fields.
     * @return A {@link FloatFilter} instance.
     */
    @Bean
    public FloatFilter floatFilter() {
        return new FloatFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.Instant} fields.
     * @return An {@link InstantFilter} instance.
     */
    @Bean
    public InstantFilter instantFilter() {
        return new InstantFilter();
    }

    /**
     * Creates a bean for filtering {@link Integer} fields.
     * @return An {@link IntegerFilter} instance.
     */
    @Bean
    public IntegerFilter integerFilter() {
        return new IntegerFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.LocalDate} fields.
     * @return A {@link LocalDateFilter} instance.
     */
    @Bean
    public LocalDateFilter localDateFilter() {
        return new LocalDateFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.LocalDateTime} fields.
     * @return A {@link LocalDateTimeFilter} instance.
     */
    @Bean
    public LocalDateTimeFilter localDateTimeFilter() {
        return new LocalDateTimeFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.LocalTime} fields.
     * @return A {@link LocalTimeFilter} instance.
     */
    @Bean
    public LocalTimeFilter localTimeFilter() {
        return new LocalTimeFilter();
    }

    /**
     * Creates a bean for filtering {@link Long} fields.
     * @return A {@link LongFilter} instance.
     */
    @Bean
    public LongFilter longFilter() {
        return new LongFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.OffsetDateTime} fields.
     * @return An {@link OffsetDateTimeFilter} instance.
     */
    @Bean
    public OffsetDateTimeFilter offsetDateTimeFilter() {
        return new OffsetDateTimeFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.OffsetTime} fields.
     * @return An {@link OffsetTimeFilter} instance.
     */
    @Bean
    public OffsetTimeFilter offsetTimeFilter() {
        return new OffsetTimeFilter();
    }

    /**
     * Creates a bean for filtering {@link Short} fields.
     * @return A {@link ShortFilter} instance.
     */
    @Bean
    public ShortFilter shortFilter() {
        return new ShortFilter();
    }

    /**
     * Creates a bean for filtering {@link String} fields.
     * @return A {@link StringFilter} instance.
     */
    @Bean
    public StringFilter stringFilter() {
        return new StringFilter();
    }

    /**
     * Creates a bean for filtering {@link java.util.UUID} fields.
     * @return A {@link UuidFilter} instance.
     */
    @Bean
    public UuidFilter uuidFilter() {
        return new UuidFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.Year} fields.
     * @return A {@link YearFilter} instance.
     */
    @Bean
    public YearFilter yearFilter() {
        return new YearFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.YearMonth} fields.
     * @return A {@link YearMonthFilter} instance.
     */
    @Bean
    public YearMonthFilter yearMonthFilter() {
        return new YearMonthFilter();
    }

    /**
     * Creates a bean for filtering {@link java.time.ZonedDateTime} fields.
     * @return A {@link ZonedDateTimeFilter} instance.
     */
    @Bean
    public ZonedDateTimeFilter zonedDateTimeFilter() {
        return new ZonedDateTimeFilter();
    }
}
