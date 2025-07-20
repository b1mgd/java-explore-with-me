package ru.practicum.service;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.dto.*;
import ru.practicum.model.dto.params.*;
import ru.practicum.model.entity.*;
import ru.practicum.model.entity.utility.Sort;
import ru.practicum.model.entity.utility.State;
import ru.practicum.model.entity.utility.Status;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EventServiceDefault implements EventServicePrivate, EventServicePublic, EventServiceAdmin {

    private static final String APP = "evm-main-service";

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;

    private final EventMapper eventMapper;
    private final ParticipationRequestMapper requestMapper;

    private final StatClient statClient;

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

        Pageable pageable = PageRequest.of(
                from / size,
                size,
                org.springframework.data.domain.Sort.by("id")
        );

        List<Event> events = eventRepository.findAllByInitiator_Id(userId, pageable);
        log.info("Получен список событий: {}", events);

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
        log.info("Событие сохранено: {}", savedEvent);

        return eventMapper.mapToEventDto(savedEvent);
    }

    /**
     * [PRIVATE] Поиск конкретного события, опубликованного текущим пользователем
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto findSavedEventById(long userId, long eventId) {
        Event event = getEventByUserIdAndId(userId, eventId);
        log.info("Получено событие: {}", event);

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
        State state = getState(eventPatch);

        eventUserPatchCorrect(userId, eventId, eventPatch);
        Event event = getEventByUserIdAndId(userId, eventId);
        Category category = getCategory(eventPatch, event);
        eventMapper.updateEventFromUserPatch(event, category, state, eventPatch);

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие обновлено: {}", updatedEvent);

        return eventMapper.mapToEventDto(updatedEvent);
    }

    /**
     * [PRIVATE] Получение всех заявок по событию пользователя
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findAllOtherUsersParticipationRequests(long userId, long eventId) {
        List<ParticipationRequest> requests = requestRepository.findAllByEvent_Initiator_idAndEvent_Id(userId, eventId);

        log.info("Получен список запросов на участие в событии: {}", requests);

        return requests.stream()
                .map(requestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    /**
     * [PRIVATE] Модерация всех направленных заявок на участие в событии пользователя
     */
    @Override
    public EventStatusUpdateDto reviewAllEventParticipationRequests(EventParamParticipationStatus param) {
        long userId = param.getUserId();
        long eventId = param.getEventId();
        EventStatusUpdateRequest updateRequest = param.getEventStatusUpdateRequest();
        List<Long> requestIds = updateRequest.getRequestIds();

        Event event = getEventByUserIdAndId(userId, eventId);
        long limit = event.getParticipantLimit();
        long participants = event.getConfirmedRequests();

        Status status = switch (updateRequest.getStatus()) {
            case CONFIRMED -> Status.CONFIRMED;
            case REJECTED -> Status.REJECTED;
        };

        participantsLimitReached(status, participants, limit);

        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(requestIds);
        requestsStatusPending(requests);

        List<ParticipationRequest> confirmedRequests;
        List<ParticipationRequest> rejectedRequests;

        if (status == Status.REJECTED) {
            confirmedRequests = Collections.emptyList();
            rejectedRequests = requests.stream()
                    .peek(req -> req.setStatus(Status.REJECTED))
                    .collect(Collectors.toList());
        } else {
            confirmedRequests = new ArrayList<>();
            rejectedRequests = new ArrayList<>();

            for (ParticipationRequest request : requests) {

                if (limit > participants) {
                    request.setStatus(Status.CONFIRMED);
                    confirmedRequests.add(request);
                    participants++;
                } else {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(request);
                }
            }
        }

        if (!confirmedRequests.isEmpty()) {
            requestRepository.saveAll(confirmedRequests);
        }

        if (!rejectedRequests.isEmpty()) {
            requestRepository.saveAll(rejectedRequests);
        }

        event.setConfirmedRequests(participants);
        eventRepository.save(event);

        log.info("Разбор заявок. confirmed requests: {}, rejectedRequests: {}", confirmedRequests, rejectedRequests);

        return EventStatusUpdateDto.builder()
                .confirmedRequests(
                        confirmedRequests.stream()
                                .map(requestMapper::mapToRequestDto)
                                .collect(Collectors.toList())
                )
                .rejectedRequests(
                        rejectedRequests.stream()
                                .map(requestMapper::mapToRequestDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    /**
     * [ADMIN] Поиск всех событий по параметрам
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDto> findAllEvents(EventParamFindAllAdmin param) {
        List<Event> events = findAllByQueryDSLAdmin(param);
        log.info("Получен список событий по параметрам динамического запроса: {}", events);

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

        State state = getState(eventPatch);
        Event event = getEventByIdAdmin(eventId);
        Category category = getCategory(eventPatch, event);

        eventMapper.updateEventFromAdminPatch(event, category, state, eventPatch);

        Event updatedEvent = eventRepository.save(event);
        log.info("Событие успешно прошло модерацию администратором: {}", updatedEvent);

        return eventMapper.mapToEventDto(updatedEvent);
    }

    /**
     * [PUBLIC] Получение списка событий, удовлетворяющего параметрам
     */
    @Override
    public List<EventShortDto> findAllEvents(EventParamFindAllPublic param, HttpServletRequest request) {
        if (request.getRequestURI().equals("/events")) {
            statClient.hit(
                    HitPost.builder()
                            .app(APP)
                            .uri(request.getRequestURI())
                            .ip(request.getRemoteAddr())
                            .build()
            );
        }

        collectionHasNoNulls(param.getCategories());

        List<Event> events = findAllByQueryDSLPublic(param);

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        String baseUrl = request.getRequestURI();
        String ip = request.getRemoteAddr();

        List<Long> eventIds = events
                .stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> eventViews = new HashMap<>();
        List<String> uris = new ArrayList<>();

        for (Long eventId : eventIds) {
            String uri = baseUrl + "/" + eventId;
            uris.add(uri);

            statClient.hit(
                    HitPost.builder()
                            .app(APP)
                            .uri(uri)
                            .ip(ip)
                            .build()
            );
        }

        List<StatsDto> stats = statClient.findAllStats(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now().plusMinutes(1),
                uris,
                true
        );

        for (StatsDto stat : stats) {
            int slashIndex = stat.getUri().lastIndexOf('/');
            long eventId = Long.parseLong(stat.getUri().substring(slashIndex + 1));
            eventViews.put(eventId, stat.getHits());
        }

        for (Event event : events) {
            long eventId = event.getId();
            event.setViews(eventViews.get(eventId));
        }

        List<Event> updatedEvents = eventRepository.saveAll(events);

        log.info("Получен список событий по динамическим параметрам: {}", updatedEvents);

        return events.stream()
                .map(eventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    /**
     * [PUBLIC] Получение опубликованного события
     */
    @Override
    public EventDto findByEventId(Long eventId, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();

        HitPost hitPost = HitPost.builder()
                .app(APP)
                .uri(uri)
                .ip(ip)
                .build();

        statClient.hit(hitPost);
        log.info("Информация по публичному запросу направлена в сервис статистики: {}", hitPost);

        Event event = getEventByIdAndStatePublic(eventId);

        List<StatsDto> stats = statClient.findAllStats(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.now().plusMinutes(1),
                List.of(request.getRequestURI()),
                true);

        long views = stats.isEmpty() ? event.getViews() : stats.get(0).getHits();
        event.setViews(views);
        log.info("Получено событие: {}", event);

        return eventMapper.mapToEventDto(event);
    }

    private void participantsLimitReached(Status status, long participants, long limit) {
        if (status == Status.CONFIRMED && participants >= limit) {
            throw new ConflictException(String.format("На событие зарегистрировано максимальное количество участников. " +
                    "Participants: %d. Limit: %d ", participants, limit));
        }
    }

    private void requestsStatusPending(List<ParticipationRequest> requests) {
        for (ParticipationRequest request : requests) {
            if (request.getStatus() != Status.PENDING) {
                throw new ConflictException("Заявка уже была отработана со статусом: " + request.getStatus());
            }
        }
    }

    private void collectionHasNoNulls(List<Category> categories) {
        if (categories != null && categories.stream().anyMatch(Objects::isNull)) {
            throw new BadRequestException("Поиск по категориям не принимает null");
        }
    }

    private void eventAdminPatchCorrect(long eventId, EventAdminPatch eventAdminPatch) {
        if (eventAdminPatch.getEventDate() != null) {
            eventDateCorrectAdmin(eventAdminPatch.getEventDate());
        }

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
            throw new BadRequestException("Попытка создать мероприятие менее чем за 1 ч. до начала");
        }
    }

    private void eventUserPostCorrect(long userId, EventUserPost eventUserPost) {
        LocalDateTime eventDate = eventUserPost.getEventDate();
        eventDateCorrectUser(eventDate);
    }

    private void eventUserPatchCorrect(long userId, long eventId, EventUserPatch eventUserPatch) {
        eventNotPublished(eventId);
        if (eventUserPatch.getEventDate() != null) {
            eventDateCorrectUser(eventUserPatch.getEventDate());
        }

        Event event = getEventByUserIdAndId(userId, eventId);
        eventDateCorrectUser(event.getEventDate());
    }

    private void userExists(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException(String.format("Пользователь с id: %d не был найден", userId));
        }
    }

    private void eventDateCorrectUser(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime referenceDateTime = dateTime.minusHours(2);

        if (referenceDateTime.isBefore(now)) {
            throw new BadRequestException("Попытка создать мероприятие менее чем за 2 ч. до начала");
        }
    }

    private void eventNotPublished(long eventId) {
        State state = eventRepository.findStateById(eventId);

        if (state.equals(State.PUBLISHED)) {
            throw new ConflictException("Внесение изменений в опубликованное мероприятие не допускается");
        }
    }

    private State getState(EventUserPatch eventPatch) {
        if (eventPatch.getStateAction() != null) {
            return switch (eventPatch.getStateAction()) {
                case SEND_TO_REVIEW -> State.PENDING;
                case CANCEL_REVIEW -> State.CANCELED;
            };
        } else {
            return State.PENDING;
        }
    }

    private State getState(EventAdminPatch eventPatch) {
        if (eventPatch.getStateAction() != null) {
            return switch (eventPatch.getStateAction()) {
                case PUBLISH_EVENT -> State.PUBLISHED;
                case REJECT_EVENT -> State.CANCELED;
            };
        } else {
            return State.PENDING;
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

    private Event getEventByUserIdAndId(long userId, long eventId) {
        return eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Мероприятие с eventId: %d не было найдено", eventId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с userId: %d не был найден", userId)));
    }

    private Category getCategory(EventUserPatch eventPatch, Event event) {
        if (eventPatch.getCategory() != null) {
            return getCategoryById(eventPatch.getCategory());

        } else {
            return event.getCategory();
        }
    }

    private Category getCategory(EventAdminPatch eventPatch, Event event) {
        if (eventPatch.getCategory() != null) {
            return getCategoryById(eventPatch.getCategory());

        } else {
            return event.getCategory();
        }
    }

    private Category getCategoryById(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с catId: %d не была найдена", catId)));
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
        List<Category> categories = param.getCategories() == null ? Collections.emptyList() : param.getCategories();
        Boolean paid = param.getPaid();
        LocalDateTime rangeStart = param.getRangeStart();
        LocalDateTime rangeEnd = param.getRangeEnd();
        Boolean onlyAvailable = param.getOnlyAvailable();

        BooleanExpression predicate = Expressions.TRUE;

        if (text != null && !text.isBlank()) {
            BooleanExpression annotation = event.annotation.toLowerCase().contains(text.toLowerCase());
            BooleanExpression description = event.description.toLowerCase().contains(text.toLowerCase());
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
