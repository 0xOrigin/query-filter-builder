package io.github._0xorigin.queryfilterbuilder.configs;

import io.github._0xorigin.queryfilterbuilder.QueryFilterBuilder;
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

/**
 * The main Spring configuration class for the Query Filter Builder.
 * This class sets up all the necessary beans and wires them together to create the final
 * {@link QueryFilterBuilder} instance.
 */
@Configuration
@EnableConfigurationProperties(QueryFilterBuilderProperties.class)
public class QueryFilterBuilderConfig {

    /**
     * Creates the {@link FilterParser} bean.
     * @param properties The configuration properties.
     * @return A {@link FilterParserImp} instance.
     */
    @Bean
    public FilterParser filterParser(QueryFilterBuilderProperties properties) {
        return new FilterParserImp(properties);
    }

    /**
     * Creates the {@link SortParser} bean.
     * @param properties The configuration properties.
     * @return A {@link SortParserImp} instance.
     */
    @Bean
    public SortParser sortParser(QueryFilterBuilderProperties properties) {
        return new SortParserImp(properties);
    }

    /**
     * Extracts the JPA {@link Metamodel} from the {@link EntityManagerFactory}.
     * @param entityManagerFactory The application's entity manager factory.
     * @return The {@link Metamodel} instance.
     */
    @Bean
    public Metamodel metamodel(EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.getMetamodel();
    }

    /**
     * Creates the {@link PathGenerator} bean.
     * @param metamodel The JPA metamodel.
     * @param properties The configuration properties.
     * @param localizationService The service for localized messages.
     * @param <T> The generic type of the entity.
     * @return A {@link FieldPathGenerator} instance.
     */
    @Bean
    public <T> PathGenerator<T> pathGenerator(Metamodel metamodel, QueryFilterBuilderProperties properties, LocalizationService localizationService) {
        return new FieldPathGenerator<>(metamodel, properties, localizationService);
    }

    /**
     * Creates the {@link FilterBuilder} bean.
     * @param fieldPathGenerator The path generator service.
     * @param filterParser The filter parser service.
     * @param filterFieldRegistry The registry of filterable fields.
     * @param filterOperatorRegistry The registry of filter operators.
     * @param localizationService The service for localized messages.
     * @param <T> The generic type of the entity.
     * @return A {@link FilterBuilderImp} instance.
     */
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

    /**
     * Creates the {@link SortBuilder} bean.
     * @param fieldPathGenerator The path generator service.
     * @param sortParser The sort parser service.
     * @param <T> The generic type of the entity.
     * @return A {@link SortBuilderImp} instance.
     */
    @Bean
    public <T> SortBuilder<T> sortBuilder(
        PathGenerator<T> fieldPathGenerator,
        SortParser sortParser
    ) {
        return new SortBuilderImp<>(fieldPathGenerator, sortParser);
    }

    /**
     * Creates the main {@link QueryFilterBuilder} bean.
     * @param filterBuilder The filter builder service.
     * @param sortBuilder The sort builder service.
     * @param <T> The generic type of the entity.
     * @return A {@link QueryFilterBuilderImp} instance.
     */
    @Bean
    public <T> QueryFilterBuilder<T> queryFilterBuilder(
        FilterBuilder<T> filterBuilder,
        SortBuilder<T> sortBuilder
    ) {
        return new QueryFilterBuilderImp<>(filterBuilder, sortBuilder);
    }
}
