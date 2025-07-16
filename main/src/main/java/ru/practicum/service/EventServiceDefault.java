package ru.practicum.service;

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
import ru.practicum.model.dto.params.EventParamFindAll;
import ru.practicum.model.dto.params.EventParamFindAllUserEvents;
import ru.practicum.model.dto.params.EventParamParticipationStatus;
import ru.practicum.model.dto.params.EventParamUserPatch;
import ru.practicum.model.entity.Category;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.QEvent;
import ru.practicum.model.entity.User;
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
public class EventServiceDefault implements EventServicePrivate, EventServiceAdmin {

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
        checkIfUserExists(userId);

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
        checkIfEventUserPostIsCorrect(userId, eventPost);

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
        EventUserPatch eventPatch = new EventUserPatch();

        checkIfEventUserPatchIsCorrect(userId, eventId, eventPatch);

        Event event = getEventByIdAndUserId(userId, eventId);
        Category category = getCategoryById(eventPatch.getCategory());
        eventMapper.updateEventFromUserPatch(event, category, eventPatch);

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
    public List<EventDto> findAllEvents(EventParamFindAll param) {
        List<Event> events = findAllByQueryDSL(param);
        log.info("Получены результаты в соответствии с динамическим запросов: {}", events);

        return events.stream()
                .map(eventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }

    /**
     * [ADMIN] Отклонение / публикация события
     */
    @Override
    public EventDto updateEventAdmin(long eventId, EventAdminPatch eventPatch) {
        checkIfEventAdminPatchIsCorrect(eventId, eventPatch);

        State state = switch (eventPatch.getStateAction()) {
            case PUBLISH_EVENT -> State.PUBLISHED;
            case REJECT_EVENT -> State.CANCELLED;
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

    private void checkIfEventAdminPatchIsCorrect(long eventId, EventAdminPatch eventAdminPatch) {
//        checkIfAdminEventDateIsCorrect(eventAdminPatch.getEventDate());
        Event event = getEventByIdAdmin(eventId);
        checkIfAdminEventDateIsCorrect(event.getEventDate());
        checkIfEventIsPending(eventId);
    }

    private void checkIfEventIsPending(long eventId) {
        State state = eventRepository.findStateById(eventId);

        if (!state.equals(State.PENDING)) {
            throw new ConflictException("Модерация событий, не ожидающих одобрения, не допускается");
        }
    }

    private void checkIfAdminEventDateIsCorrect(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime referenceDateTime = dateTime.minusHours(1);

        if (referenceDateTime.isBefore(now)) {
            throw new ConflictException("Попытка создать мероприятие менее чем за 2 ч. до начала");
        }
    }

    private void checkIfEventUserPostIsCorrect(long userId, EventUserPost eventUserPost) {
        LocalDateTime eventDate = eventUserPost.getEventDate();
        checkIfUserEventDateIsCorrect(eventDate);
    }

    private void checkIfEventUserPatchIsCorrect(long userId, long eventId, EventUserPatch eventUserPatch) {
        checkIfEventIsPublished(eventId);
        checkIfUserEventDateIsCorrect(eventUserPatch.getEventDate());

        Event event = getEventByIdAndUserId(userId, eventId);
        checkIfUserEventDateIsCorrect(event.getEventDate());
    }

    private void checkIfUserExists(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException(String.format("Пользователь с id: %d не был найден", userId));
        }
    }

    private void checkIfUserEventDateIsCorrect(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime referenceDateTime = dateTime.minusHours(2);

        if (referenceDateTime.isBefore(now)) {
            throw new ConflictException("Попытка создать мероприятие менее чем за 2 ч. до начала");
        }
    }

    private void checkIfEventIsPublished(long eventId) {
        State state = eventRepository.findStateById(eventId);

        if (state.equals(State.PUBLISHED)) {
            throw new ConflictException("Внесение изменений в опубликованное мероприятие не допускается");
        }
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

    private List<Event> findAllByQueryDSL(EventParamFindAll param) {
        QEvent event = QEvent.event;

        BooleanExpression predicate = buildBooleanExpression(param, event);

        return queryFactory
                .selectFrom(event)
                .join(event.initiator).fetchJoin()
                .join(event.category).fetchJoin()
                .where(predicate)
                .offset(param.getFrom())
                .limit(param.getSize())
                .fetch();
    }

    private BooleanExpression buildBooleanExpression(EventParamFindAll param, QEvent event) {
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
}
