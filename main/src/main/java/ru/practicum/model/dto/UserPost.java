package ru.practicum.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPost {

    @NotBlank(message = "Имя пользователя должно быть заполнено")
    @Size(min = 2, max = 250, message = "Некорректная длина имени пользователя")
    private String name;

    @NotBlank(message = "Почта должна быть указана")
    @Size(min = 6, max = 254, message = "Некорректная длина почты")
    @Email(message = "Почта не соответствует установленному формату")
    private String email;
}
