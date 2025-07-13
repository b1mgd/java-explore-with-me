package ru.practicum.service;

import ru.practicum.model.dto.CategoryDto;

import java.util.List;

public interface CategoryServicePublic {

    List<CategoryDto> findAllCategories(int from, int size);

    CategoryDto findById(long catId);
}
