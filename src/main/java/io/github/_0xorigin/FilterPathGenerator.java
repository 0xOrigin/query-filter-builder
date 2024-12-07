package io.github._0xorigin;

import io.github._0xorigin.base.ErrorWrapper;
import io.github._0xorigin.base.FilterUtils;
import io.github._0xorigin.base.PathGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import org.springframework.beans.factory.annotation.Value;

public class FilterPathGenerator<T> extends FilterUtils implements PathGenerator<T> {

    @Value("${query-filter-builder.defaults.field-delimiter:__}")
    private String FIELD_DELIMITER;
    private final EntityManager entityManager;

    public FilterPathGenerator(
        EntityManager entityManager
    ) {
        this.entityManager = entityManager;
    }

    @Override
    public Path<T> generate(Root<T> root, String field, ErrorWrapper errorWrapper) {
        String[] parts = field.split(FIELD_DELIMITER);
        Path<T> path = root;
        Class<?> currentJavaType = root.getJavaType();
        Metamodel metamodel = entityManager.getMetamodel();

        try {
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];

                ManagedType<?> modelType = metamodel.managedType(currentJavaType);
                Attribute<?, ?> attribute = modelType.getAttribute(part);

                if (!attribute.isAssociation()) {
                    path = path.get(part);
                    break;
                } else {
                    path = (path instanceof Root<?>)
                            ? ((Root<?>) path).join(part, JoinType.LEFT)
                            : ((From<?, ?>) path).join(part, JoinType.LEFT);
                }
                currentJavaType = attribute.getJavaType();
            }

            // Get the final field for the condition (e.g., "name" in "user__manager__department__name")
            String finalPart = parts[parts.length - 1];
            ManagedType<?> finalModelType = metamodel.managedType(currentJavaType);
            Attribute<?, ?> finalAttribute = finalModelType.getAttribute(finalPart);

            if (!finalAttribute.isAssociation())
                return path.get(finalPart);

            // Currently only support the singular id type
            EntityType<?> associatedEntity = metamodel.entity(finalAttribute.getJavaType());
            SingularAttribute<?, ?> idAttribute = associatedEntity.getId(associatedEntity.getIdType().getJavaType());
            return path.get(finalPart).get(idAttribute.getName());
        } catch (IllegalStateException | IllegalArgumentException e) {
            addError(
                errorWrapper,
                generateFieldError(
                    errorWrapper,
                    errorWrapper.getFilterWrapper().getValues().toString().replace("[", "").replace("]", ""),
                    e.getLocalizedMessage()
                )
            );
        }

        return null;
    }

}
