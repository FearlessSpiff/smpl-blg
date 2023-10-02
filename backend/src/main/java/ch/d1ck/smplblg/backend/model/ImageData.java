package ch.d1ck.smplblg.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public record ImageData(
        String id,
        String name,
        Image originalImage,
        Image smallImage,
        Image bigImage,
        @JsonIgnore
        Date date,
        String humanReadableDateTime,
        String cameraModel,
        String isoSpeed,
        String shutterSpeed,
        String aperture,
        String focalLength,
        String focalLength35mmEquiv

) {}
