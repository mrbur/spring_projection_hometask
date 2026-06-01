package org.example.hometask.service;

import org.example.hometask.dto.*;
import org.example.hometask.entity.DepartmentEntity;
import org.example.hometask.entity.EmployeeEntity;
import org.example.hometask.repository.DepartmentRepository;
import org.example.hometask.repository.EmployeeProjection;
import org.example.hometask.repository.ProjectedEmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private ProjectedEmployeeRepository projectedEmployeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @InjectMocks
    private EmployeeService employeeService;

    private final DepartmentEntity validDepartmentEntity = new DepartmentEntity(UUID.randomUUID(), "IT");
    private final Department validDepartment = new Department(UUID.randomUUID(), "IT");

    {
        ReflectionTestUtils.setField(employeeMapper, "departmentMapper", new DepartmentMapperImpl());
    }

    @Test
    void create_ValidEmployee_ReturnsSavedEmployee() {
        Employee employee = new Employee(null, "Иван", "Иванов", new BigDecimal("1000"), "Разработчик", validDepartment);
        EmployeeEntity savedEmployee = new EmployeeEntity(UUID.randomUUID(), "Иван", "Иванов", new BigDecimal("1000"), "Разработчик", validDepartmentEntity);

        Mockito.when(departmentRepository.existsById(validDepartment.id())).thenReturn(true);
        Mockito.when(projectedEmployeeRepository.save(any(EmployeeEntity.class))).thenReturn(savedEmployee);

        Employee result = employeeService.create(employee);

        assertNotNull(result);
        assertEquals(savedEmployee.getId(), result.id());
        Mockito.verify(projectedEmployeeRepository, Mockito.times(1)).save(any(EmployeeEntity.class));
    }

    @Test
    void create_NonExistingDepartment_ThrowsIllegalArgumentException() {
        Employee employee = new Employee(null, "Иван", "Иванов", new BigDecimal("1000"), "Разработчик", validDepartment);
        Mockito.when(departmentRepository.existsById(validDepartment.id())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.create(employee)
        );
        assertTrue(exception.getMessage().contains("Департамент с id"));
        Mockito.verify(projectedEmployeeRepository, Mockito.never()).save(any());
    }

    @Test
    void findById_ExistingId_ReturnsEmployee() {
        UUID id = UUID.randomUUID();
        var em = new EmployeeProjection() {
            @Override
            public String getFullName() {
                return "FULL NAME";
            }

            @Override
            public String getPosition() {
                return "Position";
            }

            @Override
            public String getDepartmentName() {
                return "Dep Name";
            }
        };
        Mockito.when(projectedEmployeeRepository.findProjectedById(id)).thenReturn(Optional.of(em));

        EmployeeProjection result = employeeService.findById(id);

        assertNotNull(result);
    }

    @Test
    void findById_NonExistingId_ThrowsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Mockito.when(projectedEmployeeRepository.findProjectedById(id)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.findById(id)
        );
        assertTrue(exception.getMessage().contains("Сотрудник с id"));
    }

    @Test
    void update_ExistingEmployeeAndDepartment_ReturnsUpdatedEmployee() {
        UUID id = UUID.randomUUID();
        Employee updatedData = new Employee(null, "Иван", "Новый", new BigDecimal("1500"), "Тимлид", validDepartment);
        EmployeeEntity expectedSaved = new EmployeeEntity(id, "Иван", "Новый", new BigDecimal("1500"), "Тимлид", validDepartmentEntity);

        Mockito.when(projectedEmployeeRepository.existsById(id)).thenReturn(true);
        Mockito.when(departmentRepository.existsById(validDepartment.id())).thenReturn(true);
        Mockito.when(projectedEmployeeRepository.save(any(EmployeeEntity.class))).thenReturn(expectedSaved);

        Employee result = employeeService.update(id, updatedData);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Новый", result.lastName());
    }

    @Test
    void update_NonExistingEmployee_ThrowsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Employee updatedData = new Employee(null, "Иван", "Новый", new BigDecimal("1500"), "Тимлид", validDepartment);
        Mockito.when(projectedEmployeeRepository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> employeeService.update(id, updatedData));
        Mockito.verify(projectedEmployeeRepository, Mockito.never()).save(any());
    }

    @Test
    void delete_ExistingId_DeletesEmployee() {
        UUID id = UUID.randomUUID();
        Mockito.when(projectedEmployeeRepository.existsById(id)).thenReturn(true);
        Mockito.doNothing().when(projectedEmployeeRepository).deleteById(id);

        employeeService.delete(id);

        Mockito.verify(projectedEmployeeRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void delete_NonExistingId_ThrowsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        Mockito.when(projectedEmployeeRepository.existsById(id)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> employeeService.delete(id));
        Mockito.verify(projectedEmployeeRepository, Mockito.never()).deleteById(any());
    }
}