package org.example.hometask.repository;

import org.example.hometask.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectedEmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {

    @Query("SELECT e FROM EmployeeEntity e JOIN FETCH e.department")
    List<EmployeeProjection> findAllProjected();

    Optional<EmployeeProjection> findProjectedById(UUID id);
}