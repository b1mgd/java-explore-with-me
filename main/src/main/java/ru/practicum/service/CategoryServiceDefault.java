package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.dto.CategoryChange;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.model.entity.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceDefault implements CategoryServiceAdmin, CategoryServicePublic {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    private final CategoryMapper categoryMapper;

    /**
     * [ADMIN] Добавление новой категории
     */
    @Override
    public CategoryDto save(CategoryChange categoryRequest) {
        nameUnique(categoryRequest.getName());
        Category category = categoryMapper.mapToCategory(categoryRequest);

        Category savedCategory = categoryRepository.save(category);
        log.info("Категория сохранена: {}", savedCategory);

        return categoryMapper.mapToCategoryDto(savedCategory);
    }

    /**
     * [ADMIN] Изменение существующей категории
     */
    @Override
    public CategoryDto update(CategoryChange categoryRequest, long catId) {
        Category category = getCategoryById(catId);

        if (categoryRequest.getName() != null && !categoryRequest.getName().equals(category.getName())) {
            nameUnique(categoryRequest.getName());
        }

        categoryMapper.mapToCategoryFromCategoryPatch(category, categoryRequest);

        Category savedCategory = categoryRepository.save(category);
        log.info("Категория обновлена: {}", savedCategory);

        return categoryMapper.mapToCategoryDto(savedCategory);
    }

    /**
     * [ADMIN] Удаление существующей категории
     */
    @Override
    public void delete(long catId) {
        categoryExists(catId);
        linkedWithEvents(catId);
        categoryRepository.deleteById(catId);
        log.info("Категория с catId: {} удалена", catId);
    }


    /**
     * [PUBLIC] Получение информации по всем категориям
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAllCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        log.info("Получены категории: {}", categories);

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

    private void categoryExists(long catId) {
        boolean exists = categoryRepository.existsById(catId);

        if (!exists) {
            throw new NotFoundException(String.format("Категория с catId: %d не найдена", catId));
        }
    }

    private void nameUnique(String name) {
        boolean exists = categoryRepository.existsByName(name);

        if (exists) {
            throw new ConflictException("Название категории должно быть уникально: " + name);
        }
    }

    private void linkedWithEvents(long catId) {
        boolean exists = eventRepository.existsByCategory_id(catId);

        if (exists) {
            throw new ConflictException(String.format("Категория с catId: %d связана с событиями", catId));
        }
    }
}
