package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCategoryDto {
    @NotBlank(message = "Наименование не может быть пустым")
    @Size(min = 1, max = 50, message = "Наименование должно быть не менее 1 и не более 50 символов")
    private String name;
}
