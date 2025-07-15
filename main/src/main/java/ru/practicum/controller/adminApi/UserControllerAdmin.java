package ru.practicum.controller.adminApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.UserPost;

import java.util.List;

public interface UserControllerAdmin {

    List<UserDto> findAllUsers(List<Long> ids, @PositiveOrZero Integer from, @Positive Integer size);

    UserDto save(@Valid UserPost userPost);

    void delete(Long userId);
}
