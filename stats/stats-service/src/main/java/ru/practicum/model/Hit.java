package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp = LocalDateTime.now();
}
