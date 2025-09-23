package io.github._0xorigin.queryfilterbuilder.base.builders;

import io.github._0xorigin.queryfilterbuilder.SortContext;
import io.github._0xorigin.queryfilterbuilder.base.enums.SortType;
import io.github._0xorigin.queryfilterbuilder.base.generators.PathGenerator;
import io.github._0xorigin.queryfilterbuilder.base.holders.CustomSortHolder;
import io.github._0xorigin.queryfilterbuilder.base.holders.ErrorHolder;
import io.github._0xorigin.queryfilterbuilder.base.parsers.SortParser;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrappers.SortWrapper;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the SortBuilder interface, responsible for constructing JPA Order objects from sort requests.
 *
 * @param <T> The type of the entity being sorted.
 */
public final class SortBuilderImp<T> implements SortBuilder<T> {

    private final PathGenerator<T> fieldPathGenerator;
    private final SortParser sortParser;
    private final Logger log = LoggerFactory.getLogger(SortBuilderImp.class);

    /**
     * Constructs a new SortBuilderImp with the necessary dependencies.
     *
     * @param fieldPathGenerator Generator for creating JPA Path expressions from field names.
     * @param sortParser         Parser for extracting sort requests from the source.
     */
    public SortBuilderImp(
        final PathGenerator<T> fieldPathGenerator,
        final SortParser sortParser
    ) {
        this.fieldPathGenerator = fieldPathGenerator;
        this.sortParser = sortParser;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation parses sort data from either an {@code HttpServletRequest} (query parameters) or a list of
     * {@code SortRequest} objects (request body), as provided in the {@code sortContext}. It consolidates the sorts
     * into a distinct collection, ensuring that each sort field is processed only once.
     * <p>
     * If a sort is specified in both the query parameters and the request body, the value from the request body
     * will override the one from the query parameters. It also determines whether each sort is a normal or custom
     * sort based on the context's configuration.
     */
    @Override
    public Collection<SortWrapper> getDistinctSortWrappers(@NonNull final SortContext<T> sortContext) {
        final Map<String, SortWrapper> sortWrappers = new LinkedHashMap<>();
        sortContext.getRequest().ifPresent(request ->
            sortParser.parse(request).forEach(sortWrapper ->
                sortWrappers.compute(sortWrapper.field(), (k, currentValue) -> setSortType(sortContext, sortWrapper, currentValue))
            )
        );
        sortContext.getSortRequests().ifPresent(sortRequests ->
            sortParser.parse(sortRequests).forEach(sortWrapper ->
                sortWrappers.compute(sortWrapper.originalFieldName(), (k, currentValue) -> setSortType(sortContext, sortWrapper, currentValue))
            )
        );
//        log.debug("SortWrappers: {}", sortWrappers);
        return sortWrappers.values();
    }

    private SortWrapper setSortType(final SortContext<T> sortContext, final SortWrapper sortWrapper, final SortWrapper currentValue) {
        if (isValidSortDirection(sortContext, sortWrapper))
            return sortWrapper.withSortType(SortType.NORMAL);

        if (isValidCustomSort(sortContext, sortWrapper))
            return sortWrapper.withSortType(SortType.CUSTOM);

        return currentValue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation determines whether the sort is a standard field sort or a custom sort by checking
     * the {@code sortType} of the {@code sortWrapper}. It then delegates to the appropriate private method
     * ({@code buildSortOrder} or {@code buildCustomSortOrder}) to construct the final JPA {@link Order}.
     * If the sort type is not set or not supported, it returns an empty optional.
     */
    public Optional<Order> buildOrderForWrapper(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final SortContext<T> sortContext,
        final SortWrapper sortWrapper,
        final ErrorHolder errorHolder
    ) {
        final Optional<SortType> sortType = sortWrapper.sortType();
        if (sortType.isEmpty())
            return Optional.empty();

        return switch (sortType.get()) {
            case NORMAL -> buildSortOrder(root, criteriaQuery, criteriaBuilder, sortContext, sortWrapper, errorHolder);
            case CUSTOM -> buildCustomSortOrder(root, criteriaQuery, criteriaBuilder, sortContext, sortWrapper, errorHolder);
            default -> Optional.empty();
        };
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Order> buildSortOrder(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final SortContext<T> sortContext,
        final SortWrapper sortWrapper,
        final ErrorHolder errorHolder
    ) {
        if (!isValidSortDirection(sortContext, sortWrapper))
            return Optional.empty();

        final Expression<K> expression = getExpression(root, criteriaQuery, criteriaBuilder, sortContext, sortWrapper, errorHolder);
        FilterUtils.throwServerSideExceptionIfInvalid(errorHolder);
        return getOrder(expression, criteriaBuilder, sortWrapper);
    }

    private Optional<Order> buildCustomSortOrder(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final SortContext<T> sortContext,
        final SortWrapper sortWrapper,
        final ErrorHolder errorHolder
    ) {
        if (!isValidCustomSort(sortContext, sortWrapper))
            return Optional.empty();

        final CustomSortHolder<T> customSortHolder = sortContext.getCustomSorts().get(sortWrapper.originalFieldName());
        return customSortHolder.customSortFunction().apply(root, criteriaQuery, criteriaBuilder, new SortErrorWrapper(errorHolder.bindingResult(), sortWrapper));
    }

    private <K extends Comparable<? super K> & Serializable> Optional<Order> getOrder(
        final Expression<K> expression,
        final CriteriaBuilder criteriaBuilder,
        final SortWrapper sortWrapper
    ) {
        final Sort.Direction direction = Optional.ofNullable(sortWrapper.direction()).orElse(Sort.Direction.ASC);
        return Optional.ofNullable(
            direction.isAscending() ?
                criteriaBuilder.asc(expression) :
                criteriaBuilder.desc(expression)
        );
    }

    private boolean isValidSortDirection(final SortContext<T> sortContext, final SortWrapper sortWrapper) {
        final var sorts = sortContext.getSorts();
        final boolean isSortExists = sorts.containsKey(sortWrapper.field());
        final var sortHolder = sorts.get(sortWrapper.field());
        return (
            isSortExists
                && sortHolder.directions().contains(sortWrapper.direction())
                && sortHolder.sourceTypes().contains(sortWrapper.sourceType())
        );
    }

    private boolean isValidCustomSort(final SortContext<T> sortContext, final SortWrapper sortWrapper) {
        final var customSorts = sortContext.getCustomSorts();
        final boolean isSortExists = customSorts.containsKey(sortWrapper.originalFieldName());
        final var customSortHolder = customSorts.get(sortWrapper.originalFieldName());
        return (
            isSortExists
                && customSortHolder.sourceTypes().contains(sortWrapper.sourceType())
        );
    }

    private <K extends Comparable<? super K> & Serializable> Expression<K> getExpression(
        final Root<T> root,
        final CriteriaQuery<?> criteriaQuery,
        final CriteriaBuilder criteriaBuilder,
        final SortContext<T> sortContext,
        final SortWrapper sortWrapper,
        final ErrorHolder errorHolder
    ) {
        final var sortHolder = sortContext.getSorts().get(sortWrapper.field());
        final Optional<Expression<K>> providerFunction = sortHolder.getExpression(root, criteriaQuery, criteriaBuilder);
        return providerFunction.orElseGet(() -> fieldPathGenerator.generate(root, sortWrapper.field(), sortWrapper.originalFieldName(), errorHolder.bindingResult()));
    }
}
