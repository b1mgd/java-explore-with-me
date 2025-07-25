package ru.practicum.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.entity.utility.Location;
import ru.practicum.model.entity.utility.State;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private long id;
    private long confirmedRequests;
    private long participantLimit;
    private long views;
    private boolean paid;
    private boolean requestModeration;
    private String title;
    private String annotation;
    private String description;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocalDateTime eventDate;
    private State state;
    private Location location;
    private UserDto initiator;
    private CategoryDto category;
}
