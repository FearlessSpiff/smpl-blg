package ch.d1ck.smplblg.backend.ws;

import ch.d1ck.smplblg.backend.model.ImageData;
import ch.d1ck.smplblg.backend.service.ImageMagic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import static java.util.concurrent.TimeUnit.DAYS;
import static org.springframework.http.CacheControl.maxAge;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@RestController
@RequestMapping("/api/v1")
public class ImageWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageWebService.class);
    private final ImageMagic imageMagic;

    public ImageWebService(ImageMagic imageMagic) {
        this.imageMagic = imageMagic;
    }

    @GetMapping("/images")
    public Collection<ImageData> images() {
        return this.imageMagic.images();
    }

    @GetMapping(value = "/images/{id}/{path}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage(
            @PathVariable String id,
            @PathVariable String path) {

        // Security: Validate inputs to prevent path traversal
        if (id == null || path == null || id.isBlank() || path.isBlank()) {
            LOGGER.warn("Rejected request with null or blank parameters: id={}, path={}", id, path);
            return ResponseEntity.badRequest().build();
        }

        if (id.contains("..") || path.contains("..") ||
                id.contains("/") || id.contains("\\") ||
                path.contains("\\")) {
            LOGGER.warn("Rejected suspicious image request: id={}, path={}", id, path);
            return ResponseEntity.badRequest().build();
        }

        LOGGER.debug("Serving image: {}/{}", id, path);

        try {
            InputStreamResource resource = this.imageMagic.getImage(id + "/" + path);
            return ResponseEntity
                    .ok()
                    .cacheControl(maxAge(365, DAYS))
                    .contentType(IMAGE_JPEG)
                    .body(resource);
        } catch (FileNotFoundException e) {
            LOGGER.debug("Image not found: {}/{}", id, path);
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            LOGGER.error("Error serving image: {}/{}", id, path, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}