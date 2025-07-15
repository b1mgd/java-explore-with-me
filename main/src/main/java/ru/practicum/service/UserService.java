package ru.practicum.service;

import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.params.UserParamAdminFindAll;
import ru.practicum.model.dto.UserPost;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers(UserParamAdminFindAll param);

    UserDto save(UserPost userPost);

    void delete(Long userId);
}
