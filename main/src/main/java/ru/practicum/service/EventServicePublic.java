package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.params.EventParamFindAllPublic;

import java.util.List;

public interface EventServicePublic {

    List<EventShortDto> findAllEvents(EventParamFindAllPublic param, HttpServletRequest request);

    EventDto findByEventId(Long eventId, HttpServletRequest request);
}
