package ch.d1ck.smplblg.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;

public record Image (
    String url,
    int height,
    int width,
    @JsonIgnore
    File localFile

){}
