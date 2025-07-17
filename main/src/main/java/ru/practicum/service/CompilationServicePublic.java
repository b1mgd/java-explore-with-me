package ru.practicum.service;

import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.params.CompilationParamFindAllPublic;

import java.util.List;

public interface CompilationServicePublic {

    List<CompilationDto> findAllCompilations(CompilationParamFindAllPublic param);

    CompilationDto findById(long compId);
}
