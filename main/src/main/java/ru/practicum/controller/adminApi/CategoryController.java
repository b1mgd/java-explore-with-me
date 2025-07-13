package ru.practicum.controller.adminApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.dto.CategoryRequest;

public interface CategoryController {

    CategoryDto save(@Valid CategoryRequest categoryRequest);

    CategoryDto update(@Valid CategoryRequest categoryRequest, @Positive Long catId);

    void delete(@Positive Long catId);
}
