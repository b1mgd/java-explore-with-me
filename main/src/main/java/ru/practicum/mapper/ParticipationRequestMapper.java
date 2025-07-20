package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.model.entity.ParticipationRequest;
import ru.practicum.model.entity.utility.Status;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto mapToRequestDto(ParticipationRequest requestDto);

    @Mapping(target = "status", source = "status")
    void updateRequest(@MappingTarget ParticipationRequest request,
                       Status status);
}
