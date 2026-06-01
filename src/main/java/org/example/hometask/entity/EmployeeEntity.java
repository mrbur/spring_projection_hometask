package org.example.hometask.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class EmployeeEntity {
    @Id
    private UUID id;

    private String firstName;
    private String lastName;
    private BigDecimal salary;
    private String position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentEntity department;

    public EmployeeEntity() {}

    public EmployeeEntity(UUID id, String firstName, String lastName, BigDecimal salary, String position, DepartmentEntity department) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.position = position;
        this.department = department;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public DepartmentEntity getDepartment() { return department; }
    public void setDepartment(DepartmentEntity department) { this.department = department; }
}