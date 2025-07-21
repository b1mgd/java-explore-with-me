package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.dto.RatingDto;
import ru.practicum.model.dto.RatingPost;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.Rating;
import ru.practicum.model.entity.User;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "event", source = "event")
    Rating mapToRating(RatingPost ratingPost, User user, Event event);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "eventId", source = "event.id")
    RatingDto mapToRatingDto(Rating rating);
}
