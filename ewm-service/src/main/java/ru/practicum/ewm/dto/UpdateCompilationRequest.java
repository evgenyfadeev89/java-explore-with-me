package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompilationRequest {
    @Size(max = 50, message = "Заголовок должен быть не менее 1 и не более 50 символов")
    private String title;
    private Boolean pinned;
    private Set<Long> events;
}