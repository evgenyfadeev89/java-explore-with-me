package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCommentDto {

    @Size(min = 1, max = 2000, message = "Длина комментария должна быть от 1 до 2000 символов")
    private String text;
}