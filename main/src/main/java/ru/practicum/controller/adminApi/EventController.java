package ru.practicum.controller.adminApi;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventAdminPatch;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.dto.params.EventParamFindAll;
import ru.practicum.model.entity.utility.State;
import ru.practicum.service.EventServiceAdmin;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventController implements EventControllerAdmin {

    private final EventServiceAdmin eventService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> findAllEvents(
            @RequestParam(name = "users", required = false) List<Long> users,
            @RequestParam(name = "states", required = false) List<State> states,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "rangeStart", required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false) @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("Получен запрос от клиента на поиск событий, удовлетворяющим параметрам поиска. " +
                        "users: {}, states: {}, categories: {}, rangeStart: {}, rangeEnd: {}, from: {}, size: {}",
                users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.findAllEvents(
                EventParamFindAll.builder()
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
    public EventDto updateEventAdmin(@PathVariable(name = "eventId") Long eventId,
                                     @RequestBody EventAdminPatch eventPatch) {
        log.info("Получен запрос от клиента на модерацию заявки с правами администратора. " +
                "eventId: {}, eventPatch: {}", eventId, eventPatch);
        return eventService.updateEventAdmin(eventId, eventPatch);
    }
}
