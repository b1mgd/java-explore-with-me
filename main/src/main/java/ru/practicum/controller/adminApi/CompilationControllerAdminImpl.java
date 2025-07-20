package ru.practicum.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.CompilationPatch;
import ru.practicum.model.dto.CompilationPost;
import ru.practicum.service.CompilationServiceAdmin;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CompilationControllerAdminImpl implements CompilationControllerAdmin {

    private final CompilationServiceAdmin compilationService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody CompilationPost compilationPost) {
        log.info("[ADMIN] Добавление подборки: {}", compilationPost);
        return compilationService.createCompilation(compilationPost);
    }

    @Override
    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("[ADMIN] Удаление подборки с compId: {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @Override
    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody CompilationPatch compilationPatch) {
        log.info("[ADMIN] Изменение подборки с compId: {}. compilationPatch: {}", compId, compilationPatch);
        return compilationService.updateCompilation(compId, compilationPatch);
    }
}
