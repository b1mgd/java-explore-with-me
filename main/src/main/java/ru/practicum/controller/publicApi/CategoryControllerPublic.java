package ru.practicum.controller.publicApi;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.model.dto.CategoryDto;

import java.util.List;

public interface CategoryControllerPublic {

    List<CategoryDto> findAllCategories(@PositiveOrZero Integer from, @Positive Integer size);

    CategoryDto findById(@Positive Long catId);
}
