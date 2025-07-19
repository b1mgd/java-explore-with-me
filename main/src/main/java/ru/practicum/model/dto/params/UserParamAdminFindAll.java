package ru.practicum.model.dto.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserParamAdminFindAll {
    private List<Long> ids;
    private int from;
    private int size;
}
