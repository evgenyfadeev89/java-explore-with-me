package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя должно быть не менее 2 и не более 250 символов")
    private String name;

    @NotBlank(message = "Описание description не может быть пустым")
    @Size(min = 6, max = 254, message = "Email должен быть не менее 6 и не более 254 символов")
    private String email;
}