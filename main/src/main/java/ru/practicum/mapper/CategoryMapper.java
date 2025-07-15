package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.dto.CategoryChange;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category mapToCategory(CategoryChange categoryRequest);

    @Mapping(target = "id", source = "id")
    Category mapToCategory(long id, CategoryChange categoryRequest);

    CategoryDto mapToCategoryDto(Category category);
}
