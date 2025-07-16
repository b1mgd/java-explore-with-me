package ru.practicum.controller.adminApi;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.UserDto;
import ru.practicum.model.dto.params.UserParamAdminFindAll;
import ru.practicum.model.dto.UserPost;
import ru.practicum.service.UserService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@Validated
@RequiredArgsConstructor
@Slf4j
public class UserControllerAdminImpl implements UserControllerAdmin {

    private final UserService userService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> findAllUsers(@RequestParam(required = false) List<Long> ids,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Запрос от клиента на получение списка пользователей с параметрами ids: {}, from: {}, size: {} ", ids, from, size);
        return userService.findAllUsers(
                UserParamAdminFindAll.builder()
                        .ids(ids == null ? Collections.emptyList() : ids)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto save(@RequestBody UserPost userPost) {
        log.info("Запрос от клиента на сохранение нового пользователя: {}", userPost);
        return userService.save(userPost);
    }

    @Override
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Запрос от клиента на удаление пользователя с userId: {}", userId);
        userService.delete(userId);
    }
}
