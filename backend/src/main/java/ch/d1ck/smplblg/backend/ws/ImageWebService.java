package ch.d1ck.smplblg.backend.ws;

import ch.d1ck.smplblg.backend.model.ImageData;
import ch.d1ck.smplblg.backend.service.ImageMagic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collection;

import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Controller
@RequestMapping("/api/v1")
public class ImageWebService {

    @Autowired
    private ImageMagic imageMagic;

    @GetMapping("/images")
    @ResponseBody
    public Collection<ImageData> images() {
        return this.imageMagic.images();
    }

    @GetMapping(value = "/images/{id}/{path}", produces = IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getImage(@PathVariable(value = "id") String id, @PathVariable(value = "path") String path) {
        try {
            return ResponseEntity
                    .ok()
                    .contentType(IMAGE_JPEG)
                    .body(this.imageMagic.getImage(id + "/" + path));
        } catch (IOException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }


    }
}
