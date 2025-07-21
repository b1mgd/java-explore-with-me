package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RatingMapper;
import ru.practicum.model.dto.RatingDto;
import ru.practicum.model.dto.RatingEventSummary;
import ru.practicum.model.dto.RatingPost;
import ru.practicum.model.dto.params.RatingParamPostPrivate;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.Rating;
import ru.practicum.model.entity.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RatingRepository;
import ru.practicum.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RatingServiceDefault implements RatingServicePrivate, RatingServicePublic {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private final RatingMapper ratingMapper;

    /**
     * [PRIVATE] Получение своей оценки
     */
    @Override
    @Transactional(readOnly = true)
    public RatingDto findSavedRating(long userId, long eventId) {
        userExists(userId);
        eventExists(eventId);

        Rating rating = getRatingByUserIdAndEventId(userId, eventId);
        log.info("Получена оценка события: {}", rating);

        return ratingMapper.mapToRatingDto(rating);
    }

    /**
     * [PRIVATE] Создание оценки к событию
     */
    @Override
    public RatingDto rateEvent(RatingParamPostPrivate param) {
        long userId = param.getUserId();
        long eventId = param.getEventId();
        RatingPost ratingPost = param.getRatingPost();

        ratingIsEmpty(userId, eventId);
        User user = getUserById(userId);
        Event event = getEventById(eventId);

        Rating rating = ratingMapper.mapToRating(ratingPost, user, event);
        Rating savedRating = ratingRepository.save(rating);
        log.info("Оценка события сохранена: {}", rating);

        return ratingMapper.mapToRatingDto(savedRating);
    }

    /**
     * [PRIVATE] Удаление оценки события
     */
    @Override
    public void undoRating(long userId, long eventId) {
        ratingIsPresent(userId, eventId);
        ratingRepository.deleteByUser_IdAndEvent_Id(userId, eventId);
        log.info("Оценка пользователя с userId: {}, события с eventId: {} удалена", userId, eventId);
    }

    /**
     * [PUBLIC] Получение рейтинга события
     */
    @Override
    public RatingEventSummary findEventRating(long eventId) {
        eventExists(eventId);

        RatingEventSummary summary = ratingRepository.findEventRating(eventId);
        log.info("Получен рейтинг события: {}", summary);

        return summary;
    }

    /**
     * [PUBLIC] Получение страницы оценок событий (отсортированных по рейтингу)
     */
    @Override
    public Page<RatingEventSummary> findAllEventRatings(Pageable pageable) {
        Page<RatingEventSummary> summary = ratingRepository.findAllEventRatings(pageable);
        log.info("Получена страница с оценками к событиям: {}", summary);

        return summary;
    }

    private void userExists(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException(String.format("Пользователь с userId: %d не был найден.", userId));
        }
    }

    private void eventExists(long eventId) {
        boolean exists = eventRepository.existsById(eventId);

        if (!exists) {
            throw new NotFoundException(String.format("Событие с eventId: %d не было найдено", eventId));
        }
    }

    private void ratingIsEmpty(long userId, long eventId) {
        boolean exists = ratingRepository.existsByUser_IdAndEvent_Id(userId, eventId);

        if (exists) {
            throw new ConflictException(String.format("Пользователь с userId: %d уже оценил событие с eventId: %d",
                    userId, eventId));
        }
    }

    private void ratingIsPresent(long userId, long eventId) {
        boolean exists = ratingRepository.existsByUser_IdAndEvent_Id(userId, eventId);

        if (!exists) {
            throw new NotFoundException(String.format("Оценка события пользователя userId: %d " +
                    "к событию eventId: %d не была найдена", userId, eventId));
        }
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с userId: %d не был найден.",
                        userId)));
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с eventId: %d не было найдено",
                        eventId)));
    }

    private Rating getRatingByUserIdAndEventId(long userId, long eventId) {
        return ratingRepository.findByUser_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Оценка события пользователя userId: %d " +
                        "к событию eventId: %d не была найдена", userId, eventId)));
    }
}
