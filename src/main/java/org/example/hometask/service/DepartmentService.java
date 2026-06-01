package org.example.hometask.service;

import org.example.hometask.dto.Department;
import org.example.hometask.dto.DepartmentMapper;
import org.example.hometask.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public Department create(Department department) {
        if (department == null || department.name() == null || department.name().isBlank()) {
            throw new IllegalArgumentException("Название не должно быть пустым");
        }
        return departmentMapper.toDto(departmentRepository.save(departmentMapper.toEntity(department)));
    }

    public Department findById(UUID id) {
        return departmentMapper.toDto(departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Департамент с id %s не найден".formatted(id))));
    }


    public Department update(UUID id, Department departmentData) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Невозможно обновить: Департамент с id %s не найден".formatted(id));
        }
        Department departmentToSave = new Department(id, departmentData.name());
        return departmentMapper.toDto(departmentRepository.save(departmentMapper.toEntity(departmentData)));
    }

    public void delete(UUID id) {
        if (!departmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Невозможно удалить: Департамент с id %s не найден".formatted(id));
        }
        departmentRepository.deleteById(id);
    }
}