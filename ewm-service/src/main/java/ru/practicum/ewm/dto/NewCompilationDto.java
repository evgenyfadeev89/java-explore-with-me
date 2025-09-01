package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompilationDto {
    @NotBlank(message = "Наименование не может быть пустым")
    @Size(min = 1, max = 50, message = "Наименование должно быть не менее 1 и не более 50 символов")
    private String title;
    private boolean pinned = false;
    private Set<Long> events;
}