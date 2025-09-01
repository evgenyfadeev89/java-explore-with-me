package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;

    @NotNull(message = "Статус не может быть null")
    private Status status;

    public enum Status {
        CONFIRMED,
        REJECTED
    }
}
