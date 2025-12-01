package io.github._0xorigin.queryfilterbuilder.base.generators;

import io.github._0xorigin.queryfilterbuilder.base.services.LocalizationService;
import io.github._0xorigin.queryfilterbuilder.configs.QueryFilterBuilderProperties;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.Metamodel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FieldPathGeneratorTest {

    @Mock
    private Metamodel metamodel;

    @Mock
    private LocalizationService localizationService;

    @Mock
    private QueryFilterBuilderProperties properties;

    @Mock
    private QueryFilterBuilderProperties.Defaults defaults;

    @Mock
    private Root<TestEntity> root;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ManagedType<TestEntity> rootManagedType;

    @Mock
    private ManagedType<Manager> managerManagedType;

    @Mock
    private ManagedType<Department> departmentManagedType;

    @Mock
    private Attribute<TestEntity, Manager> managerAttribute;

    @Mock
    private Attribute<Manager, Department> departmentAttribute;

    @Mock
    private Attribute<Manager, Long> idAttribute;

    @Mock
    private Attribute<Department, String> nameAttribute;

    @Mock
    private Join<TestEntity, Manager> managerJoin;

    @Mock
    private Join<Manager, Department> departmentJoin;

    @Mock
    private Path<String> namePath;

    @Mock
    private Path<Long> idPath;

    @InjectMocks
    private FieldPathGenerator<TestEntity> fieldPathGenerator;

    @BeforeEach
    void setUp() {
        when(properties.defaults()).thenReturn(defaults);
        when(defaults.fieldDelimiter()).thenReturn(".");
    }

    @Test
    void testGenerate_SimpleField_NoAssociation() {
        // Arrange
        String field = "name";
        String originalFieldName = "name";

        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("name")).thenAnswer(invocation -> nameAttribute);
        when(nameAttribute.isAssociation()).thenReturn(false);
        when(root.get("name")).thenAnswer(invocation -> namePath);

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNotNull();
        assertThat(namePath).isEqualTo(result);
        verify(bindingResult, never()).addError(any());
    }

    @Test
    void testGenerate_NestedField_WithAssociation() {
        // Arrange
        String field = "manager.department.name";
        String originalFieldName = "manager.department.name";

        // Mock root to manager
        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("manager")).thenAnswer(invocation -> managerAttribute);
        when(managerAttribute.isAssociation()).thenReturn(true);
        when(managerAttribute.getJavaType()).thenReturn(Manager.class);
        when(root.join("manager", JoinType.INNER)).thenAnswer(invocation -> managerJoin);

        // Mock manager to department
        when(metamodel.managedType(Manager.class)).thenReturn(managerManagedType);
        when(managerManagedType.getAttribute("department")).thenAnswer(invocation -> departmentAttribute);
        when(departmentAttribute.isAssociation()).thenReturn(true);
        when(departmentAttribute.getJavaType()).thenReturn(Department.class);
        when(managerJoin.join("department", JoinType.INNER)).thenAnswer(invocation -> departmentJoin);

        // Mock final field (name)
        when(metamodel.managedType(Department.class)).thenReturn(departmentManagedType);
        when(departmentManagedType.getAttribute("name")).thenAnswer(invocation -> nameAttribute);
        when(nameAttribute.isAssociation()).thenReturn(false);
        when(departmentJoin.get("name")).thenAnswer(invocation -> namePath);

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNotNull();
        assertThat(namePath).isEqualTo(result);
        verify(bindingResult, never()).addError(any());
    }

    @Test
    void testGenerate_AssociationWithId() {
        // Arrange
        String field = "manager.id";
        String originalFieldName = "manager.id";

        // Mock root to manager
        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("manager")).thenAnswer(invocation -> managerAttribute);
        when(managerAttribute.isAssociation()).thenReturn(true);
        when(managerAttribute.getJavaType()).thenReturn(Manager.class);
        when(root.join("manager", JoinType.INNER)).thenAnswer(invocation -> managerJoin);

        // Mock final field (id)
        when(metamodel.managedType(Manager.class)).thenReturn(managerManagedType);
        when(managerManagedType.getAttribute("id")).thenAnswer(invocation -> idAttribute);
        when(idAttribute.isAssociation()).thenReturn(false); // id is not an association, it's a singular attribute
        when(managerJoin.get("id")).thenAnswer(invocation -> idPath);

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNotNull();
        assertThat(idPath).isEqualTo(result);
        verify(bindingResult, never()).addError(any());
    }

    @Test
    void testGenerate_InvalidField_ThrowsIllegalArgumentException() {
        // Arrange
        String field = "invalidField";
        String originalFieldName = "invalidField";

        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("invalidField")).thenThrow(new IllegalArgumentException("Attribute not found"));
        when(bindingResult.getObjectName()).thenReturn("testEntity");

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }

    @Test
    void testGenerate_NestedField_NonAssociationInMiddle() {
        // Arrange
        String field = "manager.name.department";
        String originalFieldName = "manager.name.department";

        // Mock root to manager
        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("manager")).thenAnswer(invocation -> managerAttribute);
        when(managerAttribute.isAssociation()).thenReturn(true);
        when(managerAttribute.getJavaType()).thenReturn(Manager.class);
        when(root.join("manager", JoinType.INNER)).thenAnswer(invocation -> managerJoin);

        // Mock manager to name (non-association)
        when(metamodel.managedType(Manager.class)).thenReturn(managerManagedType);
        when(managerManagedType.getAttribute("name")).thenAnswer(invocation -> nameAttribute);
        when(nameAttribute.isAssociation()).thenReturn(false);
        when(bindingResult.getObjectName()).thenReturn("testEntity");
        when(localizationService.getMessage(anyString(), anyString())).thenReturn("Mocked message");

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }

    @Test
    void testGenerate_EmptyField() {
        // Arrange
        String field = "";
        String originalFieldName = "";

        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute(""))
            .thenThrow(new IllegalArgumentException("Empty field"));
        when(bindingResult.getObjectName()).thenReturn("testEntity");

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNull();
        verify(bindingResult).addError(any());
    }

    @Test
    void testGenerate_NullField() {
        // Arrange
        String field = null;
        String originalFieldName = null;

        // Assert
        assertThatThrownBy(() -> fieldPathGenerator.generate(root, field, originalFieldName, bindingResult))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testGenerate_ReusesExistingJoin() {
        // Arrange
        String field = "manager.department.name";
        String originalFieldName = "manager.department.name";

        // Mock root to manager attribute
        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("manager")).thenAnswer(invocation -> managerAttribute);
        when(managerAttribute.isAssociation()).thenReturn(true);
        when(managerAttribute.getJavaType()).thenReturn(Manager.class);

        // Simulate an existing join on root for 'manager'
        when(root.getJoins()).thenReturn(Set.of(managerJoin));
        when(managerJoin.getAttribute()).thenReturn((Attribute) managerAttribute);
        // Ensure attribute name matches so the join is recognized as the existing join
        when(managerAttribute.getName()).thenReturn("manager");

        // Manager to department
        when(metamodel.managedType(Manager.class)).thenReturn(managerManagedType);
        when(managerManagedType.getAttribute("department")).thenAnswer(invocation -> departmentAttribute);
        when(departmentAttribute.isAssociation()).thenReturn(true);
        when(departmentAttribute.getJavaType()).thenReturn(Department.class);
        when(managerJoin.join("department", JoinType.INNER)).thenAnswer(invocation -> departmentJoin);

        // Final field
        when(metamodel.managedType(Department.class)).thenReturn(departmentManagedType);
        when(departmentManagedType.getAttribute("name")).thenAnswer(invocation -> nameAttribute);
        when(nameAttribute.isAssociation()).thenReturn(false);
        when(departmentJoin.get("name")).thenAnswer(invocation -> namePath);

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNotNull();
        assertThat(namePath).isEqualTo(result);
        // Ensure root.join was never called because we reused the existing join
        verify(root, never()).join("manager", JoinType.INNER);
        // Ensure we did call join on the existing manager join for department
        verify(managerJoin).join("department", JoinType.INNER);
        verify(bindingResult, never()).addError(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGenerate_ReusesExistingFetch() {
        // Arrange
        String field = "manager.department.name";
        String originalFieldName = "manager.department.name";

        // Mock root to manager attribute
        when(root.getJavaType()).thenAnswer(invocation -> TestEntity.class);
        when(metamodel.managedType(TestEntity.class)).thenReturn(rootManagedType);
        when(rootManagedType.getAttribute("manager")).thenAnswer(invocation -> managerAttribute);
        when(managerAttribute.isAssociation()).thenReturn(true);
        when(managerAttribute.getJavaType()).thenReturn(Manager.class);

        // Simulate no regular joins but an existing fetch (which is also a Join) on root for 'manager'
        when(root.getJoins()).thenReturn(Set.of());
        // create a mock that implements both Join and Fetch so it can be returned from getFetches and used as a join
        @SuppressWarnings("unchecked")
        Join<TestEntity, Manager> managerJoinAndFetch = mock(Join.class, withSettings().extraInterfaces(Fetch.class));
        when(root.getFetches()).thenReturn(Set.of((Fetch) managerJoinAndFetch));
        when(managerJoinAndFetch.getAttribute()).thenReturn((Attribute) managerAttribute);
        when(managerAttribute.getName()).thenReturn("manager");

        // Manager to department
        when(metamodel.managedType(Manager.class)).thenReturn(managerManagedType);
        when(managerManagedType.getAttribute("department")).thenAnswer(invocation -> departmentAttribute);
        when(departmentAttribute.isAssociation()).thenReturn(true);
        when(departmentAttribute.getJavaType()).thenReturn(Department.class);
        when(managerJoinAndFetch.join("department", JoinType.INNER)).thenAnswer(invocation -> departmentJoin);

        // Final field
        when(metamodel.managedType(Department.class)).thenReturn(departmentManagedType);
        when(departmentManagedType.getAttribute("name")).thenAnswer(invocation -> nameAttribute);
        when(nameAttribute.isAssociation()).thenReturn(false);
        when(departmentJoin.get("name")).thenAnswer(invocation -> namePath);

        // Act
        Expression<?> result = fieldPathGenerator.generate(root, field, originalFieldName, bindingResult);

        // Assert
        assertThat(result).isNotNull();
        assertThat(namePath).isEqualTo(result);
        // Ensure root.join was never called because we reused the existing fetch join
        verify(root, never()).join("manager", JoinType.INNER);
        // Ensure we did call join on the existing manager fetch (join) for department
        verify(managerJoinAndFetch).join("department", JoinType.INNER);
        verify(bindingResult, never()).addError(any());
    }

    // Mock entity classes for testing
    static class TestEntity {
        String name;
        Manager manager;
    }

    static class Manager {
        String name;
        Department department;
        Long id;
    }

    static class Department {
        String name;
    }
}
