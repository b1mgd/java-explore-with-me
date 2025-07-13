package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.UserGetParam;
import ru.practicum.model.dto.UserPost;
import ru.practicum.model.entity.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceDefault implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers(UserGetParam param) {
        List<User> users;

        if (param.getIds().isEmpty()) {
            users = userRepository.findAllUsers(param.getFrom(), param.getSize());
        } else {
            users = userRepository.findAllUsers(param.getIds(), param.getFrom(), param.getSize());
        }

        log.info("Найдены пользователи, удовлетворяющие запросу: {}", users);

        return users.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserPost userPost) {
        User user = userMapper.mapToUser(userPost);

        try {
            User savedUser = userRepository.save(user);
            log.info("Пользователь успешно сохранен: {}", savedUser);
            return userMapper.mapToUserDto(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public void delete(Long userId) {
        checkUserExists(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с userId: {} успешно удален", userId);
    }

    private void checkUserExists(Long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException("Пользователь не найден или недоступен");
        }
    }
}
