package ru.practicum.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.dto.*;
import ru.practicum.model.dto.params.*;
import ru.practicum.model.entity.Category;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.QEvent;
import ru.practicum.model.entity.User;
import ru.practicum.model.entity.utility.Sort;
import ru.practicum.model.entity.utility.State;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventServiceDefault implements EventServicePrivate, EventServicePublic, EventServiceAdmin {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final EventMapper eventMapper;

    private final JPAQueryFactory queryFactory;

    /**
     * [PRIVATE] Поиск событий, опубликованных текущим пользователем
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findAllUserEvents(EventParamFindAllUserEvents param) {
        long userId = param.getUserId();
        int from = param.getFrom();
        int size = param.getSize();
        userExists(userId);

        List<Event> events = eventRepository.findAllEvents(userId, from, size);
        log.info("Найдены мероприятия по запросу: {}", events);

        return events.stream()
                .map(eventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    /**
     * [PRIVATE] Публикация события текущим пользователем
     */
    @Override
    public EventDto saveEvent(long userId, EventUserPost eventPost) {
        eventUserPostCorrect(userId, eventPost);

        User initiator = getUserById(userId);
        Category category = getCategoryById(eventPost.getCategory());
        Event event = eventMapper.mapToEvent(initiator, category, eventPost);
        Event savedEvent = eventRepository.save(event);
        log.info("Мероприятие сохранено: {}", savedEvent);

        return eventMapper.mapToEventDto(savedEvent);
    }

    /**
     * [PRIVATE] Поиск конкретного события, опубликованного текущим пользователем
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto findSavedEventById(long userId, long eventId) {
        Event event = getEventByIdAndUserId(userId, eventId);
        log.info("Найдено мероприятие: {}", event);

        return eventMapper.mapToEventDto(event);
    }

    /**
     * [PRIVATE] Редактирование события, опубликованного текущим пользователем
     */
    @Override
    public EventDto updateSavedEvent(EventParamUserPatch param) {
        long userId = param.getUserId();
        long eventId = param.getEventId();
        EventUserPatch eventPatch = param.getEventPatch();

        State state = switch (eventPatch.getStateAction()) {
            case SEND_TO_REVIEW -> State.PENDING;
            case CANCEL_REVIEW -> State.CANCELED;
        };

        eventUserPatchCorrect(userId, eventId, eventPatch);

        Event event = getEventByIdAndUserId(userId, eventId);
        Category category;

        if (eventPatch.getCategory() != null) {
            category = getCategoryById(eventPatch.getCategory());

        } else {
            category = event.getCategory();
        }
        eventMapper.updateEventFromUserPatch(event, category, state, eventPatch);

        Event updatedEvent = eventRepository.save(event);
        log.info("Мероприятие обновлено: {}", updatedEvent);

        return eventMapper.mapToEventDto(updatedEvent);
    }

    /**
     * [PRIVATE] Получение всех заявок по событию пользователя
     * TO-DO: ParticipationRequest Business logic
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllOtherUsersParticipationRequests(long userId, long eventId) {
        return List.of();
    }

    /**
     * [PRIVATE] Модерация всех направленных заявок на участие в событии пользователя
     * TO-DO: ParticipationRequest Business logic
     */
    /*
    1) лимит заявок = 0 или премодерация заявок = false -> подтверждение заявок не требуется
    2) нельзя подтвердить заявку если достигнут лимит заявок -> 409
    3) статус можно изменить только у заявок в состоянии ожидания -> 409
    4) если при подтверждении заявок лимит был исчерпан - остальные заявки отменяем автоматически
     */
    @Override
    public EventStatusUpdateResult reviewAllEventParticipationRequests(EventParamParticipationStatus param) {
        return null;
    }

    /**
     * [ADMIN] Поиск всех событий по параметрам
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDto> findAllEvents(EventParamFindAllAdmin param) {
        List<Event> events = findAllByQueryDSLAdmin(param);
        log.info("Получены результаты в соответствии с динамическим запросом от администратора: {}", events);

        return events.stream()
                .map(eventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }

    /**
     * [ADMIN] Отклонение / публикация события
     */
    @Override
    public EventDto updateEventAdmin(long eventId, EventAdminPatch eventPatch) {
        eventAdminPatchCorrect(eventId, eventPatch);

        State state = switch (eventPatch.getStateAction()) {
            case PUBLISH_EVENT -> State.PUBLISHED;
            case REJECT_EVENT -> State.CANCELED;
        };

        Event event = getEventByIdAdmin(eventId);
        Category category;

        if (eventPatch.getCategory() != null) {
            category = getCategoryById(eventPatch.getCategory());
        } else {
            category = event.getCategory();
        }

        eventMapper.updateEventFromAdminPatch(event, category, state, eventPatch);

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие успешно прошло модерацию администратором: {}", updatedEvent);

        return eventMapper.mapToEventDto(updatedEvent);
    }

    /**
     * [PUBLIC] Получение списка событий, удовлетворяющего параметрам
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findAllEvents(EventParamFindAllPublic param) {
        List<Event> events = findAllByQueryDSLPublic(param);
        log.info("Получены результаты в соответствии с динамическим публичным запросом : {}", events);

        return events.stream()
                .map(eventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    /**
     * [PUBLIC] Получение опубликованного события
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto findByEventId(Long eventId) {
        Event event = getEventByIdAndStatePublic(eventId);
        log.info("Получено событие: {}", event);

        return eventMapper.mapToEventDto(event);
    }

    private void eventAdminPatchCorrect(long eventId, EventAdminPatch eventAdminPatch) {
//        checkIfAdminEventDateIsCorrect(eventAdminPatch.getEventDate());
        Event event = getEventByIdAdmin(eventId);
        eventDateCorrectAdmin(event.getEventDate());
        eventStatePending(eventId);
    }

    private void eventStatePending(long eventId) {
        State state = eventRepository.findStateById(eventId);

        if (!state.equals(State.PENDING)) {
            throw new ConflictException("Модерация событий, не ожидающих одобрения, не допускается");
        }
    }

    private void eventDateCorrectAdmin(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime referenceDateTime = dateTime.minusHours(1);

        if (referenceDateTime.isBefore(now)) {
            throw new ConflictException("Попытка создать мероприятие менее чем за 2 ч. до начала");
        }
    }

    private void eventUserPostCorrect(long userId, EventUserPost eventUserPost) {
        LocalDateTime eventDate = eventUserPost.getEventDate();
        eventDateCorrectPrivate(eventDate);
    }

    private void eventUserPatchCorrect(long userId, long eventId, EventUserPatch eventUserPatch) {
        eventPublished(eventId);
        if (eventUserPatch.getEventDate() != null) {
            eventDateCorrectPrivate(eventUserPatch.getEventDate());
        }

        Event event = getEventByIdAndUserId(userId, eventId);
        eventDateCorrectPrivate(event.getEventDate());
    }

    private void userExists(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException(String.format("Пользователь с id: %d не был найден", userId));
        }
    }

    private void eventDateCorrectPrivate(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime referenceDateTime = dateTime.minusHours(2);

        if (referenceDateTime.isBefore(now)) {
            throw new ConflictException("Попытка создать мероприятие менее чем за 2 ч. до начала");
        }
    }

    private void eventPublished(long eventId) {
        State state = eventRepository.findStateById(eventId);

        if (state.equals(State.PUBLISHED)) {
            throw new ConflictException("Внесение изменений в опубликованное мероприятие не допускается");
        }
    }

    private Event getEventByIdAndStatePublic(long eventId) {
        return eventRepository.findByIdAndStateEquals(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Мероприятие с eventId: %d не было найдено", eventId)));
    }

    private Event getEventByIdAdmin(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Мероприятие с eventId: %d не было найдено", eventId)));
    }

    private Event getEventByIdAndUserId(long userId, long eventId) {
        return eventRepository.findSavedEventById(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Мероприятие с eventId: %d не было найдено", eventId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с userId: %d не был найден", userId)));
    }

    private Category getCategoryById(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с catId: %d не была найден", catId)));
    }

    private List<Event> findAllByQueryDSLAdmin(EventParamFindAllAdmin param) {
        QEvent event = QEvent.event;

        BooleanExpression predicate = buildBooleanExpressionAdmin(param, event);

        return queryFactory
                .selectFrom(event)
                .join(event.initiator).fetchJoin()
                .join(event.category).fetchJoin()
                .where(predicate)
                .offset(param.getFrom())
                .limit(param.getSize())
                .fetch();
    }

    private BooleanExpression buildBooleanExpressionAdmin(EventParamFindAllAdmin param, QEvent event) {
        BooleanExpression predicate = Expressions.TRUE;

        if (param.getUsers() != null && !param.getUsers().isEmpty()) {
            predicate = predicate.and(event.initiator.id.in(param.getUsers()));
        }

        if (param.getStates() != null && !param.getStates().isEmpty()) {
            predicate = predicate.and(event.state.in(param.getStates()));
        }

        if (param.getCategories() != null && !param.getCategories().isEmpty()) {
            predicate = predicate.and(event.category.id.in(param.getCategories()));
        }

        if (param.getRangeStart() != null) {
            predicate = predicate.and(event.eventDate.goe(param.getRangeStart()));
        }

        if (param.getRangeEnd() != null) {
            predicate = predicate.and(event.eventDate.loe(param.getRangeEnd()));
        }

        return predicate;
    }

    private List<Event> findAllByQueryDSLPublic(EventParamFindAllPublic param) {
        QEvent event = QEvent.event;
        int from = param.getFrom();
        int size = param.getSize();
        Sort sort = param.getSort();

        BooleanExpression predicate = buildBooleanExpressionPublic(param, event);
        OrderSpecifier<?> specifier = switch (sort) {
            case EVENT_DATE -> event.eventDate.desc();
            case VIEWS -> event.views.desc();
        };

        return queryFactory
                .selectFrom(event)
                .join(event.initiator).fetchJoin()
                .join(event.category).fetchJoin()
                .where(predicate)
                .orderBy(specifier)
                .offset(from)
                .limit(size)
                .fetch();
    }

    private BooleanExpression buildBooleanExpressionPublic(EventParamFindAllPublic param, QEvent event) {
        String text = param.getText();
        List<Category> categories = param.getCategories();
        Boolean paid = param.getPaid();
        LocalDateTime rangeStart = param.getRangeStart();
        LocalDateTime rangeEnd = param.getRangeEnd();
        Boolean onlyAvailable = param.getOnlyAvailable();

        BooleanExpression predicate = Expressions.TRUE;

        if (text != null && !text.isBlank()) {
            BooleanExpression annotation = event.annotation.contains(text);
            BooleanExpression description = event.description.contains(text);
            predicate = predicate.and(annotation.or(description));
        }

        if (categories != null && !categories.isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories.stream()
                    .map(Category::getId)
                    .collect(Collectors.toList())));
        }

        if (paid != null) {
            predicate = predicate.and(event.paid.eq(paid));
        }

        if (rangeStart != null) {
            predicate = predicate.and(event.eventDate.goe(rangeStart));
        } else {
            predicate = predicate.and(event.eventDate.goe(LocalDateTime.now()));
        }

        if (rangeEnd != null) {
            predicate = predicate.and(event.eventDate.loe(rangeEnd));
        }

        if (onlyAvailable != null) {
            predicate = predicate.and(event.state.eq(State.PUBLISHED));
        }

        return predicate;
    }
}
