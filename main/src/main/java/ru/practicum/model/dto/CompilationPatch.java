package ru.practicum.model.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationPatch {

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Некорректный формат названия подборки")
    private String title;

    private List<Long> events = new ArrayList<>();
}
