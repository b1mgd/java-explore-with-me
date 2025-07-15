package ru.practicum.service;

import ru.practicum.model.dto.EventAdminPatch;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.dto.params.EventParamFindAll;

import java.util.List;

public interface EventServiceAdmin {

    List<EventDto> findAllEvents(EventParamFindAll param);

    EventDto updateEventAdmin(long eventId, EventAdminPatch eventPatch);
}
