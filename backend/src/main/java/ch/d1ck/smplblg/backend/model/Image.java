package ch.d1ck.smplblg.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;

public record Image (
    String url,
    String height,
    String width,
    @JsonIgnore
    File localFile

){}
