package ru.practicum.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.service.ParticipationRequestServicePrivate;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ParticipationRequestPrivateImpl implements ParticipationRequestPrivate {

    private final ParticipationRequestServicePrivate requestService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findAllUserRequests(@PathVariable Long userId) {
        log.info("[Public] Запрос от клиента с userId: {} на получение списка направленных заявок на участие в событиях", userId);
        return requestService.findAllUserRequests(userId);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("[Public] Запрос от клиента с userId: {} на формировании заявки на участие к событию с eventId: {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @Override
    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("[Public] Запрос от клиента с userId: {} на отмену заявки на участие с requestId: {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
