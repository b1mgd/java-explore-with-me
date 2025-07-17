package ru.practicum.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.params.CompilationParamFindAllPublic;
import ru.practicum.service.CompilationServicePublic;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationControllerPublicImpl implements CompilationControllerPublic {

    private final CompilationServicePublic compilationService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> findAllCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                    @RequestParam(defaultValue = "0") Integer from,
                                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Публичный запрос от клиента на получение списка мероприятий по параметрам. " +
                "pinned: {}, from: {}, size: {}", pinned, from, size);

        return compilationService.findAllCompilations(
                CompilationParamFindAllPublic.builder()
                        .pinned(pinned)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @Override
    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto findById(@PathVariable Long compId) {
        log.info("Публичный запрос от клиента на просмотр подборки с compId: {}", compId);
        return compilationService.findById(compId);
    }
}
