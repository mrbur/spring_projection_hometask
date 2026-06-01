package org.example.hometask.service;

import org.example.hometask.dto.Employee;
import org.example.hometask.dto.EmployeeMapper;
import org.example.hometask.repository.DepartmentRepository;
import org.example.hometask.repository.EmployeeProjection;
import org.example.hometask.repository.ProjectedEmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmployeeService {

    private final ProjectedEmployeeRepository projectedEmployeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(ProjectedEmployeeRepository projectedEmployeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper) {
        this.projectedEmployeeRepository = projectedEmployeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    public Employee create(Employee employee) {
        UUID deptId = employee.department().id();
        if (!departmentRepository.existsById(deptId)) {
            throw new IllegalArgumentException("Невозможно создать сотрудника: Департамент с id %s не существует".formatted(deptId));
        }

        return employeeMapper.toDto(projectedEmployeeRepository.save(employeeMapper.toEntity(employee)));
    }

    public EmployeeProjection findById(UUID id) {
        return projectedEmployeeRepository.findProjectedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с id %s не найден".formatted(id)));
    }


    public Employee update(UUID id, Employee employeeData) {
        if (!projectedEmployeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Невозможно обновить: Сотрудник с id %s не найден".formatted(id));
        }
        
        UUID deptId = employeeData.department().id();
        if (!departmentRepository.existsById(deptId)) {
            throw new IllegalArgumentException("Невозможно обновить сотрудника: Указанный департамент с id %s не существует".formatted(deptId));
        }

        Employee employeeToSave = new Employee(
                id,
                employeeData.firstName(),
                employeeData.lastName(),
                employeeData.salary(),
                employeeData.position(),
                employeeData.department()
        );
        return employeeMapper.toDto(projectedEmployeeRepository.save(employeeMapper.toEntity(employeeToSave)));
    }

    public void delete(UUID id) {
        if (!projectedEmployeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Невозможно удалить: Сотрудник с id %s не найден".formatted(id));
        }
        projectedEmployeeRepository.deleteById(id);
    }
}