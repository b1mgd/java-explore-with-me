package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.dto.CategoryRequest;
import ru.practicum.model.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Маппинг post-запросов
     */
    Category mapToCategory(CategoryRequest categoryRequest);

    /**
     * Маппинг patch-запросов
     */
    @Mapping(target = "id", source = "id")
    Category mapToCategory(long id, CategoryRequest categoryRequest);

    CategoryDto mapToCategoryDto(Category category);
}
