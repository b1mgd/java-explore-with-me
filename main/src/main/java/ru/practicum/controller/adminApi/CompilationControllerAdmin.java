package ru.practicum.controller.adminApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.CompilationPatch;
import ru.practicum.model.dto.CompilationPost;

public interface CompilationControllerAdmin {

    CompilationDto createCompilation(@Valid CompilationPost compilationPost);

    void deleteCompilation(@Positive Long compId);

    CompilationDto updateCompilation(@Positive Long compId,
                                     @Valid CompilationPatch compilationPatch);
}
