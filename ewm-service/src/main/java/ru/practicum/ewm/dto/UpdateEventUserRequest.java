package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Аннотация должна быть не менее 20 и не более 2000 символов")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Описание должно быть не менее 20 и не более 7000 символов")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    private Boolean paid;

    private Location location;

    @Min(value = 0, message = "Значение участников не может быть отрицательным")
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Заголовок должен быть не менее 3 и не более 120 символов")
    private String title;

    private StateAction stateAction;


    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
