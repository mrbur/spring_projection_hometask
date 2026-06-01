package org.example.hometask.dto;

import org.example.hometask.entity.DepartmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartmentMapper {

    Department toDto(DepartmentEntity entity);

    DepartmentEntity toEntity(Department dto);
}