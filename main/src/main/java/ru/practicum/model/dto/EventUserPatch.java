package ru.practicum.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import ru.practicum.model.entity.utility.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUserPatch {

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW
    }

    @PositiveOrZero
    private Long participantLimit;

    private Boolean paid;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Некорректный формат названия мероприятия")
    private String title;

    @Size(min = 20, max = 2000, message = "Некорректный формат аннотации")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Некорректный формат описания")
    private String description;

    private LocalDateTime eventDate;

    private Location location;

    @Positive(message = "Некорректный id категории")
    private Long category;

    private StateAction stateAction;
}
