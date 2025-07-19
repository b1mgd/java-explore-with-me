package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.StatsDto;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    private final HitMapper hitMapper;
    private final StatsMapper statsMapper;

    @Override
    public HitDto save(HitPost hitPost) {
        Hit hit = hitMapper.mapToHit(hitPost);
        Hit savedHit = statsRepository.save(hit);
        log.info("Посещение сохранено - {}", hit);

        return hitMapper.mapToHitDto(savedHit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsDto> findAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException(String.format("Время начала start: %s не может быть после end: %s", start, end));
        }

        List<Stats> stats = statsRepository.findAllStats(start, end, uris, unique);
        log.info("Получен список Stats: {}", stats);

        return stats.stream()
                .map(statsMapper::mapToStatsDto)
                .collect(Collectors.toList());
    }
}
