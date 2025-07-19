package ru.practicum.model.dto.params;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class EventParamFindAllUserEvents {
    private long userId;
    private int from;
    private int size;
}
