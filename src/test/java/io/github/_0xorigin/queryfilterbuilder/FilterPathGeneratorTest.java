package io.github._0xorigin.queryfilterbuilder;

import io.github._0xorigin.queryfilterbuilder.base.wrapper.ErrorWrapper;
import io.github._0xorigin.queryfilterbuilder.base.wrapper.FilterWrapper;
import io.github._0xorigin.queryfilterbuilder.base.filteroperator.Operator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilterPathGeneratorTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Metamodel metamodel;

    @Mock
    private ManagedType<Object> managedType;

    @Mock
    private EntityType<?> entityType;

    @Mock
    private Root<Object> root;

    @Mock
    private Attribute<Object, Object> attribute;

    @Mock
    private Path<Object> path;

    @Mock
    private Join<Object, Object> join;

    private BindingResult bindingResult;
    private FilterPathGenerator<Object> filterPathGenerator;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        filterPathGenerator = new FilterPathGenerator<>(entityManager);
        bindingResult = new BeanPropertyBindingResult(this, "queryFilterBuilder");
        Field delimiterField = FilterPathGenerator.class.getDeclaredField("FIELD_DELIMITER");
        delimiterField.setAccessible(true);
        delimiterField.set(filterPathGenerator, "__");

        when(entityManager.getMetamodel()).thenReturn(metamodel);
        when(metamodel.managedType(any())).thenReturn(managedType);
        when(managedType.getAttribute(anyString())).thenReturn((Attribute) attribute);
    }

    @Test
    void testGenerateSimplePath() {
        // Setup
        String field = "name";
        FilterWrapper filterWrapper = new FilterWrapper(field, field, Operator.EQ, Collections.singletonList("testValue"));
        ErrorWrapper errorWrapper = new ErrorWrapper(bindingResult, filterWrapper);
        when(attribute.isAssociation()).thenReturn(false);
        when(root.get(field)).thenReturn(path);

        // Execute
        Expression<?> result = filterPathGenerator.generate(root, field, errorWrapper);

        // Verify
        assertNotNull(result);
        verify(root).get(field);
        assertFalse(errorWrapper.bindingResult().hasErrors());
    }

    @Test
    void testGenerateNestedPath() {
        // Setup
        String field = "user__department__name";
        FilterWrapper filterWrapper = new FilterWrapper(field, field, Operator.EQ, Collections.singletonList("testValue"));
        ErrorWrapper errorWrapper = new ErrorWrapper(bindingResult, filterWrapper);

        // Mock association behavior
        when(attribute.isAssociation())
                .thenReturn(true)  // for "user"
                .thenReturn(true)  // for "department"
                .thenReturn(false); // for "name"

        when(root.join("user", JoinType.LEFT)).thenReturn(join);
        when(join.join("department", JoinType.LEFT)).thenReturn(join);
        when(join.get("name")).thenReturn(path);

        // Execute
        Expression<?> result = filterPathGenerator.generate(root, field, errorWrapper);

        // Verify
        assertNotNull(result);
        verify(root).join("user", JoinType.LEFT);
        verify(join).join("department", JoinType.LEFT);
        verify(join).get("name");
    }

    @Test
    void testGenerateWithAssociationEndingPath() {
        // Setup
        String field = "department";
        FilterWrapper filterWrapper = new FilterWrapper(field, field, Operator.EQ, Collections.singletonList("testValue"));
        ErrorWrapper errorWrapper = new ErrorWrapper(bindingResult, filterWrapper);
        Class<?> idClass = Long.class; // Using Long as a common ID type

        // Mock metamodel chain
        when(attribute.isAssociation()).thenReturn(true);
        when(attribute.getJavaType()).thenReturn((Class) Department.class); // Assuming Department is the associated entity
        when(metamodel.entity(Department.class)).thenReturn((EntityType<Department>) entityType);

        // Mock ID type chain
        BasicType<?> idType = mock(BasicType.class);
        when(idType.getJavaType()).thenReturn((Class) idClass.getClass());
        when(entityType.getIdType()).thenReturn((Type) idType);

        // Mock ID attribute
        SingularAttribute<?, ?> idAttribute = mock(SingularAttribute.class);
        when(idAttribute.getName()).thenReturn("id");
        when(entityType.getId(Class.class)).thenReturn((SingularAttribute)idAttribute);

        // Mock path chain
        when(root.get(field)).thenReturn(path);
        when(path.get("id")).thenReturn(path);

        // Execute
        Expression<?> result = filterPathGenerator.generate(root, field, errorWrapper);

        // Verify
        assertNotNull(result);
        verify(root).get(field);
        verify(path).get("id");
        assertFalse(errorWrapper.bindingResult().hasErrors());
    }

    @Test
    void testGenerateWithInvalidPath() {
        // Setup
        String field = "invalidField";
        FilterWrapper filterWrapper = new FilterWrapper(field, field, Operator.EQ, Collections.singletonList("testValue"));
        ErrorWrapper errorWrapper = new ErrorWrapper(bindingResult, filterWrapper);
        when(managedType.getAttribute(field)).thenThrow(new IllegalArgumentException("Invalid field"));

        // Execute
        Expression<?> result = filterPathGenerator.generate(root, field, errorWrapper);

        // Verify
        assertNull(result);
        assertTrue(errorWrapper.bindingResult().hasErrors());
        assertEquals(1, errorWrapper.bindingResult().getAllErrors().size());
    }

    @Test
    void testGenerateWithCustomFieldDelimiter() {
        // Setup using reflection to set custom delimiter
        try {
            Field delimiterField = FilterPathGenerator.class.getDeclaredField("FIELD_DELIMITER");
            delimiterField.setAccessible(true);
            delimiterField.set(filterPathGenerator, "##");

            String field = "user##name";
            FilterWrapper filterWrapper = new FilterWrapper(field, field, Operator.EQ, Collections.singletonList("testValue"));
            ErrorWrapper errorWrapper = new ErrorWrapper(bindingResult, filterWrapper);
            when(attribute.isAssociation()).thenReturn(true).thenReturn(false);
            when(root.join("user", JoinType.LEFT)).thenReturn(join);
            when(join.get("name")).thenReturn(path);

            // Execute
            Expression<?> result = filterPathGenerator.generate(root, field, errorWrapper);

            // Verify
            assertNotNull(result);
            verify(root).join("user", JoinType.LEFT);
            verify(join).get("name");
            assertFalse(errorWrapper.bindingResult().hasErrors());

        } catch (Exception e) {
            fail("Failed to set custom delimiter: " + e.getMessage());
        }
    }

    // Helper class for type safety
    private static class Department {
        private Long id;
    }
}