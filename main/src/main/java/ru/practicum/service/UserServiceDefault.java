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
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.params.UserParamAdminFindAll;
import ru.practicum.model.dto.UserPost;
import ru.practicum.model.entity.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceDefault implements UserServiceAdmin {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers(UserParamAdminFindAll param) {
        List<Long> ids = param.getIds();
        int from = param.getFrom();
        int size = param.getSize();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));

        List<User> users = ids.isEmpty() ?
                userRepository.findAll(pageable)
                        .stream()
                        .toList() :
                userRepository.findAllByIdIn(ids, pageable);

        log.info("Найден список пользователей: {}", users);

        return users.stream()
                .map(userMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserPost userPost) {
        User user = userMapper.mapToUser(userPost);
        emailUnique(userPost.getEmail());

        User savedUser = userRepository.save(user);
        log.info("Пользователь сохранен: {}", savedUser);

        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    public void delete(long userId) {
        userExists(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с userId: {} удален", userId);
    }

    private void userExists(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException(String.format("Пользователь c userId: %d не найден", userId));
        }
    }

    private void emailUnique(String email) {
        boolean exists = userRepository.existsByEmail(email);

        if (exists) {
            throw new ConflictException(String.format("Пользователь с email: %s уже зарегистрирован", email));
        }
    }
}
