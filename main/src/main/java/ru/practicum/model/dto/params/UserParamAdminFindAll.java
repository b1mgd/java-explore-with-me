package ru.practicum.model.dto.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserParamAdminFindAll {
    private List<Long> ids;
    private Integer from;
    private Integer size;
}
