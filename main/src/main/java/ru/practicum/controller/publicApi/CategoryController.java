package ru.practicum.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.service.CategoryServicePublic;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CategoryController implements CategoryControllerPublic {

    private final CategoryServicePublic categoryService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> findAllCategories(@RequestParam(name = "from") Integer from,
                                               @RequestParam(name = "size") Integer size) {
        log.info("Получен запрос от клиента о получении списка категорий с параметрами. from: {}, size: {}", from, size);
        return categoryService.findAllCategories(from, size);
    }

    @Override
    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto findById(@PathVariable(name = "catId") Long catId) {
        log.info("Получен запрос от клиента о получении категории с catId: {}", catId);
        return categoryService.findById(catId);
    }
}
