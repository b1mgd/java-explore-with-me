package ru.practicum.controller.adminApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CategoryChange;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.service.CategoryServiceAdmin;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryControllerAdminImpl implements CategoryControllerAdmin {

    private final CategoryServiceAdmin categoryService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto save(@RequestBody CategoryChange categoryRequest) {
        log.info("Запрос от клиента на добавление новой категории: {}", categoryRequest);
        return categoryService.save(categoryRequest);
    }

    @Override
    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@RequestBody CategoryChange categoryRequest,
                              @PathVariable(name = "catId") Long catId) {
        log.info("Запрос от клиента на изменение существующей категории с catId: {}, request: {}", catId, categoryRequest);
        return categoryService.update(categoryRequest, catId);
    }

    @Override
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(name = "catId") Long catId) {
        log.info("Запрос от клиента на удаление категории с catId: {}", catId);
        categoryService.delete(catId);
    }
}
