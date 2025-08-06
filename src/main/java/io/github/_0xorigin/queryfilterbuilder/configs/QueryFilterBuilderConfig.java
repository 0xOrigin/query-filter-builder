package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilder;
import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilderExceptionHandler;
import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilderImp;
import io.github._0xorigin.queryfilterbuilder.base.builders.FilterBuilder;
import io.github._0xorigin.queryfilterbuilder.base.builders.FilterBuilderImp;
import io.github._0xorigin.queryfilterbuilder.base.builders.SortBuilder;
import io.github._0xorigin.queryfilterbuilder.base.builders.SortBuilderImp;
import io.github._0xorigin.queryfilterbuilder.base.generators.FieldPathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.parsers.FilterParser;
import io.github._0xorigin.queryfilterbuilder.base.parsers.FilterParserImp;
import io.github._0xorigin.queryfilterbuilder.base.parsers.SortParser;
import io.github._0xorigin.queryfilterbuilder.base.parsers.SortParserImp;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.registries.FilterFieldRegistry;
import io.github._0xorigin.queryfilterbuilder.registries.FilterOperatorRegistry;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QueryFilterBuilderProperties.class)
public class QueryFilterBuilderConfig {

    @Bean
    public FilterParser filterParser(QueryFilterBuilderProperties properties) {
        return new FilterParserImp(properties);
    }

    @Bean
    public SortParser sortParser(QueryFilterBuilderProperties properties) {
        return new SortParserImp(properties);
    }

    @Bean
    public Metamodel metamodel(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.getMetamodel();
    }

    @Bean
    public <T> PathGenerator<T> pathGenerator(Metamodel metamodel, QueryFilterBuilderProperties properties) {
        return new FieldPathGenerator<>(metamodel, properties);
    }

    @Bean
    public <T> FilterBuilder<T> filterBuilder(
        PathGenerator<T> fieldPathGenerator,
        FilterParser filterParser,
        FilterFieldRegistry filterFieldRegistry,
        FilterOperatorRegistry filterOperatorRegistry,
        LocalizationService localizationService
    ) {
        return new FilterBuilderImp<>(fieldPathGenerator, filterParser, filterFieldRegistry, filterOperatorRegistry, localizationService);
    }

    @Bean
    public <T> SortBuilder<T> sortBuilder(
        PathGenerator<T> fieldPathGenerator,
        SortParser sortParser
    ) {
        return new SortBuilderImp<>(fieldPathGenerator, sortParser);
    }

    @Bean
    public <T> QueryFilterBuilder<T> queryFilterBuilder(
        FilterBuilder<T> filterBuilder,
        SortBuilder<T> sortBuilder
    ) {
        return new QueryFilterBuilderImp<>(filterBuilder, sortBuilder);
    }

    @Bean
    public QueryFilterBuilderExceptionHandler queryFilterBuilderExceptionHandler(LocalizationService localizationService) {
        return new QueryFilterBuilderExceptionHandler(localizationService);
    }
}
