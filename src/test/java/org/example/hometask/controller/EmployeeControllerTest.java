package org.example.hometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hometask.dto.Department;
import org.example.hometask.dto.Employee;
import org.example.hometask.repository.EmployeeProjection;
import org.example.hometask.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EmployeeService employeeService;

    private final Department validDepartment = new Department(UUID.randomUUID(), "Разработка");

    @Test
    void createEmployee_ValidRequest_ReturnsCreated() throws Exception {
        Employee input = new Employee(null, "Иван", "Иванов", new BigDecimal("150000"), "Разработчик", validDepartment);
        UUID generatedId = UUID.randomUUID();
        Employee saved = new Employee(generatedId, "Иван", "Иванов", new BigDecimal("150000"), "Разработчик", validDepartment);

        Mockito.when(employeeService.create(any(Employee.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.lastName").value("Иванов"))
                .andExpect(jsonPath("$.salary").value(150000))
                .andExpect(jsonPath("$.position").value("Разработчик"))
                .andExpect(jsonPath("$.department.id").value(validDepartment.id().toString()));
    }

    @Test
    void createEmployee_InvalidFirstName_ReturnsBadRequest() throws Exception {
        Employee invalidInput = new Employee(null, "   ", "Иванов", new BigDecimal("150000"), "Разработчик", validDepartment);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isInternalServerError());

        Mockito.verifyNoInteractions(employeeService);
    }

    @Test
    void createEmployee_NegativeSalary_ReturnsBadRequest() throws Exception {
        Employee invalidInput = new Employee(null, "Иван", "Иванов", new BigDecimal("-500.00"), "Разработчик", validDepartment);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isInternalServerError());

        Mockito.verifyNoInteractions(employeeService);
    }

    @Test
    void createEmployee_EmptyPosition_ReturnsBadRequest() throws Exception {
        Employee invalidInput = new Employee(null, "Иван", "Иванов", new BigDecimal("150000"), "", validDepartment);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isInternalServerError());

        Mockito.verifyNoInteractions(employeeService);
    }

    @Test
    void getEmployeeById_ExistingId_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Employee employee = new Employee(id, "Петр", "Петров", new BigDecimal("120000"), "Тестировщик", validDepartment);

        Mockito.when(employeeService.findById(id)).thenReturn(new EmployeeProjection() {
            @Override
            public String getFullName() {
                return "Петр";
            }

            @Override
            public String getPosition() {
                return "Тестировщик";
            }

            @Override
            public String getDepartmentName() {
                return "Dep name";
            }
        });

        mockMvc.perform(get("/api/v1/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Петр"))
                .andExpect(jsonPath("$.position").value("Тестировщик"));
    }

    @Test
    void updateEmployee_ValidRequest_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Employee input = new Employee(null, "Иван", "Новый", new BigDecimal("160000"), "Тимлид", validDepartment);
        Employee updated = new Employee(id, "Иван", "Новый", new BigDecimal("160000"), "Тимлид", validDepartment);

        Mockito.when(employeeService.update(eq(id), any(Employee.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.lastName").value("Новый"))
                .andExpect(jsonPath("$.position").value("Тимлид"));
    }

    @Test
    void updateEmployee_InvalidRequest_ReturnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        Employee invalidInput = new Employee(null, "Иван", "Иванов", null, "Разработчик", validDepartment);

        mockMvc.perform(put("/api/v1/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isInternalServerError());

        Mockito.verifyNoInteractions(employeeService);
    }

    @Test
    void deleteEmployee_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(employeeService).delete(id);

        mockMvc.perform(delete("/api/v1/employees/{id}", id))
                .andExpect(status().isNoContent());
    }
}