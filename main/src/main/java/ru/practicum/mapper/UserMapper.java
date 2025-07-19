package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.UserPost;
import ru.practicum.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User mapToUser(UserPost userPost);

    UserDto mapToUserDto(User user);
}
