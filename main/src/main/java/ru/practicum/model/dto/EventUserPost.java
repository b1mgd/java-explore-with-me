package ru.practicum.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.entity.utility.Location;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUserPost {

    @PositiveOrZero
    private long participantLimit;

    private boolean paid;

    private boolean requestModeration;

    @NotBlank(message = "Название мероприятия должно быть заполнено")
    @Size(min = 3, max = 120, message = "Некорректный формат названия мероприятия")
    private String title;

    @NotBlank(message = "Аннотация должна быть заполнена")
    @Size(min = 20, max = 2000, message = "Некорректный формат аннотации")
    private String annotation;

    @NotBlank(message = "Описание должно быть заполнено")
    @Size(min = 20, max = 7000, message = "Некорректный формат описания")
    private String description;

    @NotNull(message = "Дата события должна быть указана")
    private LocalDateTime eventDate;

    @NotNull(message = "Местоположение должно быть указано")
    private Location location;

    @NotNull(message = "Категория должна бывать указана")
    @Positive(message = "Некорректный id категории")
    private Long category;
}
