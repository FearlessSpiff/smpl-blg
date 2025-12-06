package ch.d1ck.smplblg.backend.exception;

/**
 * Exception thrown when image metadata operations fail.
 */
public class ImageMetadataException extends RuntimeException {

    public ImageMetadataException(String message) {
        super(message);
    }

    public ImageMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
}
