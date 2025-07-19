package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.CompilationPatch;
import ru.practicum.model.dto.CompilationPost;
import ru.practicum.model.dto.params.CompilationParamFindAllPublic;
import ru.practicum.model.entity.Compilation;
import ru.practicum.model.entity.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceDefault implements CompilationServicePublic, CompilationServiceAdmin {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;

    /**
     * [PUBLIC] получение списка подборок по параметрам
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> findAllCompilations(CompilationParamFindAllPublic param) {
        boolean pinned = param.isPinned();
        int from = param.getFrom();
        int size = param.getSize();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageable);
        log.info("Найдены подборки: {}", compilations);

        return compilations.stream()
                .map(compilationMapper::mapToCompilationDto)
                .collect(Collectors.toList());
    }

    /**
     * [PUBLIC] получение конкретной подборки
     */
    @Override
    @Transactional(readOnly = true)
    public CompilationDto findById(long compId) {
        Compilation compilation = getCompilationById(compId);
        log.info("Найдена подборка: {}", compilation);

        return compilationMapper.mapToCompilationDto(compilation);
    }

    /**
     * [ADMIN] Создание подборки
     */
    @Override
    public CompilationDto createCompilation(CompilationPost compilationPost) {
        List<Event> events = getEventsFromEventIds(compilationPost.getEvents());

        Compilation compilation = compilationMapper.mapToCompilation(compilationPost, events);
        Compilation savedCompilation = compilationRepository.save(compilation);
        log.info("Подборка сохранена: {}", savedCompilation);

        return compilationMapper.mapToCompilationDto(savedCompilation);
    }

    /**
     * [ADMIN] Удаление подборки
     */
    @Override
    public void deleteCompilation(long compId) {
        compilationExists(compId);
        compilationRepository.deleteById(compId);
        log.info("Подборка с compId: {} удалена", compId);
    }

    /**
     * [ADMIN] Изменение подборки
     */
    @Override
    public CompilationDto updateCompilation(long compId, CompilationPatch compilationPatch) {
        Compilation compilation = getCompilationById(compId);
        List<Event> events = getEventsFromEventIds(compilationPatch.getEvents());
        compilationMapper.updateCompilationFromPatch(compilation, compilationPatch, events);

        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Подборка обновлена: {}", updatedCompilation);

        return compilationMapper.mapToCompilationDto(updatedCompilation);
    }

    private List<Event> getEventsFromEventIds(List<Long> eventIds) {
        if (eventIds != null && !eventIds.isEmpty()) {
            return eventRepository.findAllByIdIn((eventIds));
        } else {
            return Collections.emptyList();
        }
    }

    private Compilation getCompilationById(long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с comId: %d не найдена", compId)));
    }

    private void compilationExists(long compId) {
        boolean exists = compilationRepository.existsById(compId);

        if (!exists) {
            throw new NotFoundException(String.format("Подборка с comId: %d не найдена", compId));
        }
    }
}
