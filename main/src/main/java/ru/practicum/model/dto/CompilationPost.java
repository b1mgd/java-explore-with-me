package ru.practicum.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompilationPost {

    @NotNull(message = "Флаг закрепленности подборки должен быть указан")
    private Boolean pinned;

    @NotBlank(message = "Название подборки должно быть указано")
    @Size(min = 2, max = 120, message = "Некорректный формат названия подборки")
    private String title;

    private List<Long> events = new ArrayList<>();
}
