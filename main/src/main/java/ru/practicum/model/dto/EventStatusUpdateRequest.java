package ru.practicum.model.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Статус запроса на участие должен быть указан")
    private Status status;
}
