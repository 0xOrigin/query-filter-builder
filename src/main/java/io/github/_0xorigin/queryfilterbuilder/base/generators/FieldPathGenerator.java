package io.github._0xorigin.queryfilterbuilder.base.generators;

import io.github._0xorigin.queryfilterbuilder.base.enums.MessageKey;
import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import org.springframework.validation.BindingResult;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The default implementation of {@link PathGenerator}.
 * It uses the JPA {@link Metamodel} to traverse delimited paths (e.g., "customer.address.city"),
 * creating necessary joins and resolving the final expression.
 *
 * @param <T> The type of the root entity.
 */
public final class FieldPathGenerator<T> implements PathGenerator<T> {

    private final Metamodel metamodel;
    private final QueryFilterBuilderProperties properties;
    private final LocalizationService localizationService;

    /**
     * Constructs a new FieldPathGenerator.
     *
     * @param metamodel           The JPA metamodel, used for reflection on the entity structure.
     * @param properties          Configuration properties for the query builder, such as the field delimiter.
     * @param localizationService Service for retrieving localized error messages.
     */
    public FieldPathGenerator(
        Metamodel metamodel,
        QueryFilterBuilderProperties properties,
        LocalizationService localizationService
    ) {
        this.metamodel = metamodel;
        this.properties = properties;
        this.localizationService = localizationService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation parses the {@code field} string using the configured delimiter.
     * It traverses the entity graph from the {@code root}, creating inner joins for each association in the path.
     * If an intermediate part of the path is not an association, or if the path is invalid, an error is added
     * to the {@code bindingResult} and {@code null} is returned.
     * If the final part of the path is an association, it automatically resolves to the ID of that association.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <K extends Comparable<? super K> & Serializable> Expression<K> generate(Root<T> root, String field, String originalFieldName, BindingResult bindingResult) {
        final String FIELD_DELIMITER = properties.defaults().fieldDelimiter();

        Objects.requireNonNull(root, "Root cannot be null");
        Objects.requireNonNull(field, "Field cannot be null");
        Objects.requireNonNull(originalFieldName, "Original field name cannot be null");
        Objects.requireNonNull(bindingResult, "Binding result cannot be null");

        final String[] parts = FilterUtils.splitWithEscapedDelimiter(field, FIELD_DELIMITER);
        Path<T> path = root;
        Class<?> currentJavaType = root.getJavaType();

        try {
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];

                ManagedType<?> modelType = metamodel.managedType(currentJavaType);
                Attribute<?, ?> attribute = modelType.getAttribute(part);

                if (!attribute.isAssociation()) {
                    FilterUtils.addFieldError(
                        bindingResult,
                        originalFieldName,
                        "",
                        localizationService.getMessage(MessageKey.NON_ASSOCIATION_IN_INTERMEDIATE_PATH.getCode(), part)
                    );
                    return null;
                }

                From<?, ?> from = (path instanceof Root<?>) ? (Root<?>) path : (From<?, ?>) path;

                // Try to reuse an existing join for this attribute on the current path (root or from)
                Join<?, ?> existingJoin = findExistingJoin(from, part);
                path = existingJoin != null ? (Path<T>) existingJoin : from.join(part, JoinType.INNER);

                currentJavaType = attribute.getJavaType();
            }

            // Get the final field for the condition (e.g., "name" in "user.manager.department.name")
            String finalPart = parts.length > 0 ? parts[parts.length - 1] : "";
            ManagedType<?> finalModelType = metamodel.managedType(currentJavaType);
            Attribute<?, ?> finalAttribute = finalModelType.getAttribute(finalPart);

            if (!finalAttribute.isAssociation())
                return path.get(finalPart);

            // Currently only support the singular id type
            EntityType<?> associatedEntity = metamodel.entity(finalAttribute.getJavaType());
            SingularAttribute<?, ?> idAttribute = associatedEntity.getId(associatedEntity.getIdType().getJavaType());
            return path.get(finalPart).get(idAttribute.getName());
        } catch (IllegalStateException | IllegalArgumentException exception) {
            FilterUtils.addFieldError(
                bindingResult,
                originalFieldName,
                "",
                exception.getLocalizedMessage()
            );
            return null;
        }
    }

    // Helper to find an existing join on a From by attribute name to avoid duplicate joins
    private Join<?, ?> findExistingJoin(From<?, ?> from, String attributeName) {
        if (from == null || attributeName == null)
            return null;

        for (Join<?, ?> join : getAllJoins(from)) {
            Attribute<?, ?> attr = safeGetAttribute(join);
            if (attr != null && attributeName.equals(attr.getName())) {
                return join;
            }
        }
        return null;
    }

    // Collect joins from both getJoins() and getFetches() depending on configuration
    private Set<Join<?, ?>> getAllJoins(From<?, ?> from) {
        Set<Join<?, ?>> all = new HashSet<>();
        Set<? extends Join<?, ?>> joins = from.getJoins();
        if (joins != null && !joins.isEmpty())
            all.addAll(joins);

        // Only include fetches if configured to do so. Some users prefer to control fetch handling
        Set<? extends Fetch<?, ?>> fetches = from.getFetches();
        if (fetches != null && !fetches.isEmpty()) {
            for (Fetch<?, ?> fetch : fetches) {
                if (fetch instanceof Join<?, ?> join)
                    all.add(join);
            }
        }
        return all;
    }

    // Safe wrapper around Join.getAttribute() because some JPA implementations may throw from it
    private Attribute<?, ?> safeGetAttribute(Join<?, ?> join) {
        if (join == null)
            return null;
        try {
            return join.getAttribute();
        } catch (Exception ignored) {
            return null;
        }
    }
}
