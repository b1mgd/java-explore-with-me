package ru.practicum.service;

import ru.practicum.model.dto.RatingDto;
import ru.practicum.model.dto.params.RatingParamPostPrivate;

public interface RatingServicePrivate {

    RatingDto findSavedRating(long userId, long eventId);

    RatingDto rateEvent(RatingParamPostPrivate param);

    void undoRating(long userId, long eventId);
}
