package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.Stats;
import ru.practicum.model.dto.StatsDto;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatsDto mapToStatsDto(Stats stats);
}
