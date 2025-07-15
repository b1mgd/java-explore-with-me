package ru.practicum.model.dto.params;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EventParamFindAllUserEvents {
    private long userId;
    private int from;
    private int size;
}
