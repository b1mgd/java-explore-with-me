package ru.practicum.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private long id;
    private boolean isLike;
    private LocalDateTime createdOn;
    private long userId;
    private long eventId;
}
