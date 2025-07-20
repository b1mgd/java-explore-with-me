package ru.practicum.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {
    private long id;
    private long confirmedRequests;
    private long views;
    private boolean paid;
    private String title;
    private String annotation;
    private LocalDateTime eventDate;
    private UserDto initiator;
    private CategoryDto category;
}
