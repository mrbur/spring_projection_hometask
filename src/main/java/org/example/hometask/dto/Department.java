package org.example.hometask.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record Department(UUID id, @NotBlank(message = "Название департамента не должно быть пустым") String name) {
    public Department {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}

