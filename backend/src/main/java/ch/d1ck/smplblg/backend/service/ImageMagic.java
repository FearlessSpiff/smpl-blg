package ch.d1ck.smplblg.backend.service;

import ch.d1ck.smplblg.backend.model.Image;
import jakarta.annotation.PostConstruct;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.*;
import static org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.*;
import static org.imgscalr.Scalr.Method.ULTRA_QUALITY;

@Service
public class ImageMagic {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageMagic.class);

    @Value("${image-path}")
    private String imagePath;

    private final SortedSet<Image> images = new TreeSet<>(comparing(Image::dateTime).reversed());

    @PostConstruct
    public void init() {
        initThumbnails();
        initCache();
    }

    public Collection<Image> images() {
        return images;
    }

    public InputStreamResource getImage(String path) throws IOException {
        FileUrlResource fileUrlResource = new FileUrlResource(this.imagePath + "/" + path);

        return new InputStreamResource(fileUrlResource.getInputStream());
    }


    private void initThumbnails() {
        try (Stream<Path> stream = Files.list(Paths.get(this.imagePath))) {
            stream.filter(Files::isDirectory)
                    .forEach(ImageMagic::createThumbnailIfNeeded);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initCache() {
        try (Stream<Path> stream = Files.list(Paths.get(this.imagePath))) {
            stream.filter(Files::isDirectory)
                    .forEach(this::addToCache);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToCache(Path directoryPath) {
        String[] imageFiles = directoryPath.toFile().list((dir, name) -> name.endsWith("jpeg") || name.endsWith("JPEG") || name.endsWith("jpg") || name.endsWith("JPG"));
        if ((imageFiles != null ? imageFiles.length : 0) == 2) {
            String originalFilename = imageFiles[0];
            String thumbFilename = imageFiles[1];
            if (imageFiles[0].contains("thumb-")) {
                originalFilename = imageFiles[1];
                thumbFilename = imageFiles[0];
            }

            final JpegImageMetadata jpegImageMetadata;
            try {
                File originalImageFile = Paths.get(directoryPath.toString(), originalFilename).toFile();
                BufferedImage bufferedImage = ImageIO.read(originalImageFile);
                jpegImageMetadata = (JpegImageMetadata) Imaging.getMetadata(originalImageFile);
                Image image = new Image(
                        directoryPath.getFileName().toString(),
                        directoryPath.getFileName().toString(),
                        "images/" + directoryPath.getFileName() + "/" + originalFilename,
                        Integer.toString(bufferedImage.getHeight()),
                        Integer.toString(bufferedImage.getWidth()),
                        "images/" + directoryPath.getFileName() + "/" + thumbFilename,
                        exifOf(jpegImageMetadata, TIFF_TAG_DATE_TIME),
                        exifOf(jpegImageMetadata, EXIF_TAG_FOCAL_LENGTH),
                        exifOf(jpegImageMetadata, EXIF_TAG_SHUTTER_SPEED_VALUE),
                        exifOf(jpegImageMetadata, EXIF_TAG_APERTURE_VALUE),
                        exifOf(jpegImageMetadata, EXIF_TAG_ISO)
                );
                LOGGER.info("adding '" + image + "' to cache ...");
                this.images.add(image);
            } catch (IOException | ImageReadException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String exifOf(JpegImageMetadata jpegImageMetadata, TagInfo exifTag) throws ImageReadException {
        return jpegImageMetadata.findEXIFValueWithExactMatch(exifTag) != null ? jpegImageMetadata.findEXIFValueWithExactMatch(exifTag).getValue().toString() : "";
    }

    private static void createThumbnailIfNeeded(Path directoryPath) {
        if (hasThumbnail(directoryPath)) {
            return;
        }

        Optional<Path> optionalImagePath = findImagePath(directoryPath);
        if (optionalImagePath.isEmpty()) {
            return;
        }
        Path imagePath = optionalImagePath.get();
        try (FileOutputStream fos = new FileOutputStream(imagePath.getParent().toString() + "/thumb-" + imagePath.getFileName().toString());
             OutputStream os = new BufferedOutputStream(fos)) {

            LOGGER.info("creating thumbnail for '" + imagePath.getFileName() + "'" );

            BufferedImage originalImage = ImageIO.read(imagePath.toFile());
            final JpegImageMetadata originalJpegMetadata = (JpegImageMetadata) Imaging.getMetadata(imagePath.toFile());
            BufferedImage resizedImage = Scalr.resize(originalImage, ULTRA_QUALITY, 720);
            File tempResizedFile = new File(imagePath.getParent().toString() + "/temp-" + imagePath.getFileName().toString());
            ImageIO.write(resizedImage, "jpeg", tempResizedFile);

            new ExifRewriter().updateExifMetadataLossless(tempResizedFile, os, originalJpegMetadata.getExif().getOutputSet());

            tempResizedFile.delete();

        } catch (IOException | ImageReadException | ImageWriteException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean hasThumbnail(Path directoryPath) {
        try (Stream<Path> stream = Files.list(directoryPath)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .anyMatch(filePath -> filePath.getFileName().toString().startsWith("thumb-"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<Path> findImagePath(Path directoryPath) {
        try (Stream<Path> stream = Files.list(directoryPath)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(filePath -> filePath.getFileName().toString().endsWith(".JPG") ||
                            filePath.getFileName().toString().endsWith(".JPEG") ||
                            filePath.getFileName().toString().endsWith(".jpg") ||
                            filePath.getFileName().toString().endsWith(".jpeg"))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
