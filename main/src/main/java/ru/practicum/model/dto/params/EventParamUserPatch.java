package ru.practicum.model.dto.params;

import lombok.*;
import ru.practicum.model.dto.EventUserPatch;

@Data
@Builder
@AllArgsConstructor
public class EventParamUserPatch {
    private long userId;
    private long eventId;
    private EventUserPatch eventPatch;
}
