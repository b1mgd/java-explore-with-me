package ru.practicum.service;

import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.UserGetParam;
import ru.practicum.model.dto.UserPost;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers(UserGetParam param);

    UserDto save(UserPost userPost);

    void delete(Long userId);
}
