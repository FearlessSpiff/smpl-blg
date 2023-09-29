package ch.d1ck.smplblg.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public record ImageData(
        String id,
        String name,
        Image originalImage,
        Image smallImage,
        Image bigImage,
        @JsonIgnore
        LocalDateTime dateTime,
        String humanReadableDateTime,
        String model

) {}
