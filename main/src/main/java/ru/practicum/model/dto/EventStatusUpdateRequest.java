package ru.practicum.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatusUpdateRequest {

    public enum Status {
        CONFIRMED, REJECTED
    }

    private List<Long> requestIds = new ArrayList<>();

    @NotNull(message = "Статус запроса на участие должен быть указан")
    private Status status;
}
