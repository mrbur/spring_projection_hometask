package org.example.hometask.service;

import org.example.hometask.dto.*;
import org.example.hometask.entity.DepartmentEntity;
import org.example.hometask.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Spy
    private DepartmentMapper employeeMapper = new DepartmentMapperImpl();

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void create_ValidDepartment_ReturnsSavedDepartment() {
        Department department = new Department(null, "HR");
        DepartmentEntity savedDepartment = new DepartmentEntity(UUID.randomUUID(), "HR");
        
        Mockito.when(departmentRepository.save(any(DepartmentEntity.class))).thenReturn(savedDepartment);

        Department result = departmentService.create(department);

        assertNotNull(result);
        assertEquals(savedDepartment.getId(), result.id());
        assertEquals("HR", result.name());
        Mockito.verify(departmentRepository, Mockito.times(1)).save(any(DepartmentEntity.class));
    }

    @Test
    void findById_ExistingId_ReturnsDepartment() {
        UUID id = UUID.randomUUID();
        DepartmentEntity department = new DepartmentEntity(id, "IT");
        
        Mockito.when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        Department result = departmentService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("IT", result.name());
    }

    @Test
    void update_ExistingId_ReturnsUpdatedDepartment() {
        UUID id = UUID.randomUUID();
        Department updatedData = new Department(null, "New Name");
        DepartmentEntity expectedSaved = new DepartmentEntity(id, "New Name");

        Mockito.when(departmentRepository.existsById(id)).thenReturn(true);
        Mockito.when(departmentRepository.save(any(DepartmentEntity.class))).thenReturn(expectedSaved);

        Department result = departmentService.update(id, updatedData);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("New Name", result.name());
    }

    @Test
    void delete_ExistingId_DeletesDepartment() {
        UUID id = UUID.randomUUID();
        Mockito.when(departmentRepository.existsById(id)).thenReturn(true);
        Mockito.doNothing().when(departmentRepository).deleteById(id);

        departmentService.delete(id);
        Mockito.verify(departmentRepository, Mockito.times(1)).deleteById(id);
    }

}