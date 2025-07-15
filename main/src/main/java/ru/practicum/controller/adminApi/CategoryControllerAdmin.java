package ru.practicum.controller.adminApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.dto.CategoryChange;

public interface CategoryControllerAdmin {

    CategoryDto save(@Valid CategoryChange categoryRequest);

    CategoryDto update(@Valid CategoryChange categoryRequest, @Positive Long catId);

    void delete(@Positive Long catId);
}
