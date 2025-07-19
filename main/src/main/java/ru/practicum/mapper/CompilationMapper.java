package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.model.dto.CompilationPatch;
import ru.practicum.model.dto.CompilationPost;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.entity.Compilation;
import ru.practicum.model.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    @Mapping(target = "events", source = "events")
    Compilation mapToCompilation(CompilationPost compilationPost, List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", source = "events")
    void updateCompilationFromPatch(@MappingTarget Compilation compilation,
                                    CompilationPatch compilationPatch,
                                    List<Event> events);

    CompilationDto mapToCompilationDto(Compilation compilation);
}
