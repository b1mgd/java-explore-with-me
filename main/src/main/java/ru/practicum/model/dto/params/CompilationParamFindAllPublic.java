package ru.practicum.model.dto.params;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CompilationParamFindAllPublic {
    private boolean pinned;
    private int from;
    private int size;
}
