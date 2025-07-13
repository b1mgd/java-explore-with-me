package ru.practicum.service;

import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.dto.CategoryRequest;

public interface CategoryServiceAdmin {

    CategoryDto save(CategoryRequest categoryRequest);

    CategoryDto update(CategoryRequest categoryRequest, long catId);

    void delete(long catId);
}
