package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.Hit;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.ViewStats;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public HitDto save(HitPost hitPost) {
        Hit hit = statsMapper.mapToHit(hitPost);
        Hit savedHit = statsRepository.save(hit);
        log.info("Посещение ресурса сохранено {}", hit);

        return statsMapper.mapToHitDto(savedHit);
    }

    @Override
    public ViewStats findAllStats(LocalDateTime start, LocalDateTime end, String[] uri, Boolean unique) {
        statsRepository.
        return null;
    }
}
