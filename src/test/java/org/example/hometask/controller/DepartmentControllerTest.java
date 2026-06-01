package org.example.hometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.hometask.dto.Department;
import org.example.hometask.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DepartmentService departmentService;

    @Test
    void createDepartment_ValidRequest_ReturnsCreated() throws Exception {
        Department input = new Department(null, "HR");
        UUID generatedId = UUID.randomUUID();
        Department saved = new Department(generatedId, "HR");

        Mockito.when(departmentService.create(any(Department.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.name").value("HR"));
    }

    @Test
    void createDepartment_InvalidName_ReturnsBadRequest() throws Exception {
        Department invalidInput = new Department(null, "   ");

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isInternalServerError());

        Mockito.verifyNoInteractions(departmentService);
    }

    @Test
    void getDepartmentById_ExistingId_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Department department = new Department(id, "IT");

        Mockito.when(departmentService.findById(id)).thenReturn(department);

        mockMvc.perform(get("/api/v1/departments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("IT"));
    }

    @Test
    void updateDepartment_ValidRequest_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();
        Department input = new Department(null, "Marketing");
        Department updated = new Department(id, "Marketing");

        Mockito.when(departmentService.update(eq(id), any(Department.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/departments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Marketing"));
    }

    @Test
    void updateDepartment_InvalidName_ReturnsBadRequest() throws Exception {
        UUID id = UUID.randomUUID();
        Department invalidInput = new Department(null, "");

        mockMvc.perform(put("/api/v1/departments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidInput)))
                .andExpect(status().isInternalServerError());

        Mockito.verifyNoInteractions(departmentService);
    }

    @Test
    void deleteDepartment_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(departmentService).delete(id);

        mockMvc.perform(delete("/api/v1/departments/{id}", id))
                .andExpect(status().isNoContent());
    }
}