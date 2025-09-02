package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.ewm.model.Location;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank(message = "Описание annotation не может быть пустым")
    @Size(min = 20, max = 2000, message = "Описание должно быть не менее 1 и не более 50 символов")
    private String annotation;

    @NotNull(message = "Категория не может быть пустой")
    private Long category;

    @NotBlank(message = "Описание description не может быть пустым")
    @Size(min = 20, max = 7000, message = "Описание должно быть не менее 1 и не более 50 символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @Min(value = 0, message = "Значение участников не может быть отрицательным")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Описание description не может быть пустым")
    @Size(min = 3, max = 120, message = "Описание title должно быть не менее 1 и не более 50 символов")
    private String title;
}
