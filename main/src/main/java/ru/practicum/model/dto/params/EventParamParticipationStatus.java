package ru.practicum.model.dto.params;

import lombok.*;
import ru.practicum.model.dto.EventStatusUpdateRequest;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class EventParamParticipationStatus {
    private long userId;
    private long eventId;
    private EventStatusUpdateRequest eventStatusUpdateRequest;
}
