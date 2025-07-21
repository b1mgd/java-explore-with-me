package ru.practicum.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.RatingDto;
import ru.practicum.model.dto.RatingPost;
import ru.practicum.model.dto.params.RatingParamPostPrivate;
import ru.practicum.service.RatingServicePrivate;

@RestController
@RequestMapping("/users/{userId}/events/{eventId}/rating")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RatingControllerPrivateImpl implements RatingControllerPrivate {

    private final RatingServicePrivate ratingService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public RatingDto findSavedRating(@PathVariable Long userId,
                                     @PathVariable Long eventId) {
        log.info("[PRIVATE] Получение своей оценки события. userId: {}, eventId: {}", userId, eventId);
        return ratingService.findSavedRating(userId, eventId);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto rateEvent(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody RatingPost ratingPost) {
        log.info("[PRIVATE] Оценка события. userId: {}, eventId: {}, ratingPost: {}", userId, eventId, ratingPost);
        return ratingService.rateEvent(
                RatingParamPostPrivate.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .ratingPost(ratingPost)
                        .build()
        );
    }

    @Override
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void undoRating(@PathVariable Long userId,
                           @PathVariable Long eventId) {
        log.info("[PRIVATE] Удаление оценки события. userId: {}, eventId: {}", userId, eventId);
        ratingService.undoRating(userId, eventId);
    }
}
