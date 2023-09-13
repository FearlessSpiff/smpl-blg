package ch.d1ck.smplblg.backend.model;

public record Image(
        String id,
        String name,
        String url,
        String thumbUrl,
        String dateTime,
        String focalLength,
        String shutterSpeed,
        String aperture,
        String iso

) {}
