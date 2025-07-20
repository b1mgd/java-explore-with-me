package ru.practicum.mapper;

import org.mapstruct.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.entity.Category;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.User;
import ru.practicum.model.entity.utility.State;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", source = "eventUserPost.location")
    @Mapping(target = "state", expression = "java(ru.practicum.model.entity.utility.State.PENDING)")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    Event mapToEvent(User initiator, Category category, EventUserPost eventUserPost);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "state")
    void updateEventFromUserPatch(@MappingTarget Event event,
                                  Category category,
                                  State state,
                                  EventUserPatch eventUserPatch);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "state", source = "state")
    void updateEventFromAdminPatch(@MappingTarget Event event,
                                   Category category,
                                   State state,
                                   EventAdminPatch eventAdminPatch);

    EventDto mapToEventDto(Event event);

    EventShortDto mapToEventShortDto(Event event);
}
