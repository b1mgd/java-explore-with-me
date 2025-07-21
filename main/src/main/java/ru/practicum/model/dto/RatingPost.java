package ru.practicum.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RatingPost {

    @NotNull(message = "Лайк / дизлайк должен быть указан")
    private Boolean isLike;
}
