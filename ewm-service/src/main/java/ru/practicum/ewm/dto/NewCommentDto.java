package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 1, max = 2000, message = "Длина комментария должна быть от 1 до 2000 символов")
    private String text;

    @NotNull(message = "ID события должно быть указано")
    private Long eventId;
}