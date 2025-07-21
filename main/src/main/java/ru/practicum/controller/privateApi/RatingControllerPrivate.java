package ru.practicum.controller.privateApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.model.dto.RatingDto;
import ru.practicum.model.dto.RatingPost;

public interface RatingControllerPrivate {

    RatingDto findSavedRating(@Positive Long userId, @Positive Long eventId);

    RatingDto rateEvent(@Positive Long userId,
                        @Positive Long eventId,
                        @Valid RatingPost ratingPost);

    void undoRating(@Positive Long userId, @Positive Long eventId);
}
