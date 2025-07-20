package ru.practicum.service;

import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.dto.CategoryChange;

public interface CategoryServiceAdmin {

    CategoryDto save(CategoryChange categoryRequest);

    CategoryDto update(CategoryChange categoryRequest, long catId);

    void delete(long catId);
}
