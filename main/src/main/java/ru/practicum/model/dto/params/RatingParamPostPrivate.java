package ru.practicum.model.dto.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.model.dto.RatingPost;

@Getter
@Builder
@AllArgsConstructor
public class RatingParamPostPrivate {
    private long userId;
    private long eventId;
    private RatingPost ratingPost;
}
