package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.model.Hit;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;

@Mapper(componentModel = "spring")
public interface HitMapper {

    Hit mapToHit(HitPost hitPost);

    HitDto mapToHitDto(Hit hit);
}
