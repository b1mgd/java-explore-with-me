package ru.practicum.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventAdminPatch;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.dto.params.EventParamFindAllAdmin;
import ru.practicum.model.entity.utility.State;
import ru.practicum.service.EventServiceAdmin;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerAdminImpl implements EventControllerAdmin {

    private final EventServiceAdmin eventService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> findAllEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<State> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("[ADMIN] Поиск событий, по параметрам поиска. " +
                        "users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.findAllEvents(
                EventParamFindAllAdmin.builder()
                        .users(users)
                        .states(states)
                        .categories(categories)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @Override
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateEventAdmin(@PathVariable Long eventId,
                                     @RequestBody EventAdminPatch eventPatch) {
        log.info("[ADMIN] Модерация заявки администратором. eventId: {}, eventPatch: {}", eventId, eventPatch);
        return eventService.updateEventAdmin(eventId, eventPatch);
    }
}
