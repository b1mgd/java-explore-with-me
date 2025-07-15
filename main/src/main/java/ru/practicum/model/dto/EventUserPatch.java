package ru.practicum.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EventUserPatch extends EventUserPost {

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW
    }

    @NotBlank(message = "Действие пользователя должно быть указано")
    private StateAction stateAction;
}
