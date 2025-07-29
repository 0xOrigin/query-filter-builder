package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.filters.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterFieldConfig {

    @Bean
    public BigDecimalFilter bigDecimalFilter() {
        return new BigDecimalFilter();
    }

    @Bean
    public BigIntegerFilter bigIntegerFilter() {
        return new BigIntegerFilter();
    }

    @Bean
    public BooleanFilter booleanFilter() {
        return new BooleanFilter();
    }

    @Bean
    public ByteFilter byteFilter() {
        return new ByteFilter();
    }

    @Bean
    public CharacterFilter characterFilter() {
        return new CharacterFilter();
    }

    @Bean
    public DoubleFilter doubleFilter() {
        return new DoubleFilter();
    }

    @Bean
    public FloatFilter floatFilter() {
        return new FloatFilter();
    }

    @Bean
    public InstantFilter instantFilter() {
        return new InstantFilter();
    }

    @Bean
    public IntegerFilter integerFilter() {
        return new IntegerFilter();
    }

    @Bean
    public LocalDateFilter localDateFilter() {
        return new LocalDateFilter();
    }

    @Bean
    public LocalDateTimeFilter localDateTimeFilter() {
        return new LocalDateTimeFilter();
    }

    @Bean
    public LocalTimeFilter localTimeFilter() {
        return new LocalTimeFilter();
    }

    @Bean
    public LongFilter longFilter() {
        return new LongFilter();
    }

    @Bean
    public OffsetDateTimeFilter offsetDateTimeFilter() {
        return new OffsetDateTimeFilter();
    }

    @Bean
    public OffsetTimeFilter offsetTimeFilter() {
        return new OffsetTimeFilter();
    }

    @Bean
    public ShortFilter shortFilter() {
        return new ShortFilter();
    }

    @Bean
    public StringFilter stringFilter() {
        return new StringFilter();
    }

    @Bean
    public UuidFilter uuidFilter() {
        return new UuidFilter();
    }

    @Bean
    public YearFilter yearFilter() {
        return new YearFilter();
    }

    @Bean
    public YearMonthFilter yearMonthFilter() {
        return new YearMonthFilter();
    }

    @Bean
    public ZonedDateTimeFilter zonedDateTimeFilter() {
        return new ZonedDateTimeFilter();
    }
}
