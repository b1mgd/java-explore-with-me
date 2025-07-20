package ru.practicum.model.dto.params;

import lombok.*;
import ru.practicum.model.dto.EventStatusUpdateRequest;

@Data
@Builder
@AllArgsConstructor
public class EventParamParticipationStatus {
    private long userId;
    private long eventId;
    private EventStatusUpdateRequest eventStatusUpdateRequest;
}
