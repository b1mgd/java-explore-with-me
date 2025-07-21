package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RatingEventSummary {
    private long eventId;
    private long likes;
    private long dislikes;

    @JsonProperty("totalRating")
    public long getTotalRating() {
        return likes - dislikes;
    }
}
