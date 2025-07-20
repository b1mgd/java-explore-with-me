package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatClientImpl implements StatClient {

    private final RestClient restClient;

    @Override
    public HitDto hit(HitPost hitPost) {
        log.info("Клиент направляет запрос о сохранении посещения: {}", hitPost);

        HitDto hitDto = restClient.post()
                .uri("/hit")
                .body(hitPost)
                .retrieve()
                .body(HitDto.class);

        log.info("Сервер вернул ответ о посещении: {}", hitDto);
        return hitDto;
    }

    @Override
    public List<StatsDto> findAllStats(LocalDateTime start,
                                       LocalDateTime end,
                                       List<String> uri,
                                       Boolean unique) {

        log.info("Клиент направил запрос на статистику: start={}, end={}, uri={}, unique={}",
                start, end, uri, unique);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("unique", unique);

        if (uri != null && !uri.isEmpty()) {
            uri.forEach(u -> uriBuilder.queryParam("uris", u));
        }

        String uriString = uriBuilder.build().toUriString();

        ResponseEntity<List<StatsDto>> response = restClient.get()
                .uri(uriString)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<StatsDto> stats = response.getBody();
        log.info("Клиент получил ответ: {}", stats);

        return stats;
    }
}

