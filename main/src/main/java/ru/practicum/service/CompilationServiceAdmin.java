package ru.practicum.service;

import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.CompilationPatch;
import ru.practicum.model.dto.CompilationPost;

public interface CompilationServiceAdmin {

    CompilationDto createCompilation(CompilationPost compilationPost);

    void deleteCompilation(long compId);

    CompilationDto updateCompilation(long compId, CompilationPatch compilationPatch);
}
