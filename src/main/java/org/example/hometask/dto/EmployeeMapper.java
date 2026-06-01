package org.example.hometask.dto;

import org.example.hometask.entity.EmployeeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = DepartmentMapper.class)
public interface EmployeeMapper {

    Employee toDto(EmployeeEntity entity);

    EmployeeEntity toEntity(Employee dto);
}