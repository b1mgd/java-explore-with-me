package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.model.dto.CategoryChange;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category mapToCategory(CategoryChange categoryRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mapToCategoryFromCategoryPatch(@MappingTarget Category category,
                                        CategoryChange categoryChange);

    CategoryDto mapToCategoryDto(Category category);
}
