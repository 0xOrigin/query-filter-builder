package io.github._0xorigin.queryfilterbuilder.base.generators;

import io.github._0xorigin.queryfilterbuilder.base.utils.FilterUtils;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import org.springframework.validation.BindingResult;

import java.io.Serializable;

public final class FieldPathGenerator<T> implements PathGenerator<T> {

    private final Metamodel metamodel;
    private final QueryFilterBuilderProperties properties;

    public FieldPathGenerator(Metamodel metamodel, QueryFilterBuilderProperties properties) {
        this.metamodel = metamodel;
        this.properties = properties;
    }

    @Override
    public <K extends Comparable<? super K> & Serializable> Expression<K> generate(Root<T> root, String field, String originalFieldName, BindingResult bindingResult) {
        final String FIELD_DELIMITER = properties.defaults().fieldDelimiter();
        final String[] parts = FilterUtils.splitWithEscapedDelimiter(field, FIELD_DELIMITER);
        Path<T> path = root;
        Class<?> currentJavaType = root.getJavaType();

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
            FilterUtils.addError(
                bindingResult,
                FilterUtils.generateFieldError(
                    bindingResult,
                    originalFieldName,
                    "",
                    e.getLocalizedMessage()
                )
            );
            return null;
        }
    }
}
