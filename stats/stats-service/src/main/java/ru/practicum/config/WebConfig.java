package ru.practicum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addFormatter(new Formatter<LocalDateTime>() {

            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public LocalDateTime parse(String text, Locale locale) {
                return LocalDateTime.parse(text, formatter);
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return formatter.format(object);
            }
        });
    }
}
