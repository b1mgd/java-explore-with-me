package ru.practicum.controller.publicApi;


import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.model.dto.CompilationDto;

import java.util.List;

public interface CompilationControllerPublic {

    List<CompilationDto> findAllCompilations(Boolean pinned, @PositiveOrZero Integer from, @Positive Integer size);

    CompilationDto findById(@Positive Long compId);
}
