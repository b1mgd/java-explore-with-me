package ru.practicum.model.dto.params;

import lombok.Builder;
import lombok.Data;
import ru.practicum.model.entity.Category;
import ru.practicum.model.entity.utility.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventParamFindAllPublic {
    private String text;
    private List<Category> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Sort sort;
    private Integer from;
    private Integer size;
}
