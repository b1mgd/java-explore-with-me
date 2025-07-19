package ru.practicum.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HitPost {

    @NotBlank(message = "Название сервиса не может быть пустым")
    @Size(min = 2, max = 50, message = "Некорректное название сервиса")
    private String app;

    @NotBlank(message = "Uri не может быть пустым")
    @Size(min = 3, max = 255, message = "Некорректный формат uri")
    private String uri;

    @NotBlank(message = "Ip нe может быть пустым")
    @Size(min = 7, max = 15, message = "Некорректный формат ip")
    private String ip;
}
