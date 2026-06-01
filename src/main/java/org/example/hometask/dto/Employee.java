package org.example.hometask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record Employee(UUID id, @NotBlank(message = "Имя не должно быть пустым") String firstName, String lastName,
                       @NotNull(message = "Заработная плата не должна быть пустой")
                       @Positive(message = "Заработная плата должна быть положительным числом")BigDecimal salary,
                       @NotBlank(message = "Позиция не должна быть пустой") String position,
                       @NotNull Department department) {
    public Employee {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
