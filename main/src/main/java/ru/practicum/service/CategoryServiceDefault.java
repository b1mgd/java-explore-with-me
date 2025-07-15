package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.dto.CategoryChange;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.entity.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceDefault implements CategoryServiceAdmin, CategoryServicePublic {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * [ADMIN] Добавление новой категории
     */
    @Override
    public CategoryDto save(CategoryChange categoryRequest) {
        checkNameUnique(categoryRequest.getName());
        Category category = categoryMapper.mapToCategory(categoryRequest);

        Category savedCategory = categoryRepository.save(category);
        log.info("Новая категория сохранена: {}", savedCategory);

        return categoryMapper.mapToCategoryDto(savedCategory);
    }

    /**
     * [ADMIN] Изменение существующей категории
     */
    @Override
    public CategoryDto update(CategoryChange categoryRequest, long catId) {
        checkCategoryExists(catId);
        checkNameUnique(categoryRequest.getName());

        Category category = categoryMapper.mapToCategory(catId, categoryRequest);
        Category savedCategory = categoryRepository.save(category);
        log.info("Категория успешно обновлена: {}", savedCategory);

        return categoryMapper.mapToCategoryDto(savedCategory);
    }

    /**
     * [ADMIN] Удаление существующей категории
     * TO-DO: существуют события, связанные с категорией -> 409
     */
    @Override
    public void delete(long catId) {
        checkCategoryExists(catId);
        categoryRepository.deleteById(catId);
        log.info("Категория с catId: {} успешно удалена", catId);
    }


    /**
     * [PUBLIC] Получение информации по всем категориям
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategories(int from, int size) {
        List<Category> categories = categoryRepository.findAllCategories(from, size);
        log.info("Получены все категории, удовлетворяющие запросу: {}", categories);

        return categories.stream()
                .map(categoryMapper::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    /**
     * [PUBLIC] Получение информации о конкретной категории
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(long catId) {
        Category category = getCategoryById(catId);
        log.info("Получена категория: {}", category);

        return categoryMapper.mapToCategoryDto(category);
    }

    private Category getCategoryById(long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с catId: %d не найдена", catId)));
    }

    private void checkCategoryExists(long catId) {
        boolean exists = categoryRepository.existsById(catId);

        if (!exists) {
            throw new NotFoundException(String.format("Категория с catId: %d не найдена", catId));
        }
    }

    private void checkNameUnique(String name) {
        Optional<Category> category = categoryRepository.findByName(name);

        if (category.isPresent()) {
            throw new ConflictException("Попытка добавить уже существующее название категории: " + name);
        }
    }
}
