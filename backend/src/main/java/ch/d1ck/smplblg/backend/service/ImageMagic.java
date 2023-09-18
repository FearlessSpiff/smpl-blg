package ch.d1ck.smplblg.backend.service;

import ch.d1ck.smplblg.backend.model.Image;
import ch.d1ck.smplblg.backend.model.ImageData;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static ch.d1ck.smplblg.backend.service.ImageSize.*;
import static java.util.Comparator.comparing;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.*;
import static org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants.EXIF_TAG_ISO;
import static org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.TIFF_TAG_DATE_TIME;
import static org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants.TIFF_TAG_ORIENTATION;
import static org.imgscalr.Scalr.Method.ULTRA_QUALITY;

@Service
public class ImageMagic {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageMagic.class);

    @Value("${image-path}")
    private String imagePath;

    private final SortedSet<ImageData> images = new TreeSet<>(comparing(ImageData::dateTime).reversed());
    private final Collection<String> supportedFileEndings = List.of("JPEG", "JPG");
    private final DateTimeFormatter exifParser = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Collection<ImageData> images() {
        return images;
    }

    public InputStreamResource getImage(String path) throws IOException {
        FileUrlResource fileUrlResource = new FileUrlResource(this.imagePath + "/" + path);

        return new InputStreamResource(fileUrlResource.getInputStream());
    }

    @Scheduled(initialDelay = 0, fixedDelay = 300000)
    private void scanForNewImages() {
        try (Stream<Path> stream = Files.list(Paths.get(this.imagePath))) {
            stream.filter(Files::isDirectory).forEach(this::processDirectory);
            LOGGER.info("finished scanning for new images");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processDirectory(Path directoryPath) {
        if (directoryPath.getFileName().toString().startsWith(".")) {
            LOGGER.debug("skipping hidden directory '" + directoryPath + "'");
            return;
        }
        long numberOfFilesInDir = filesCount(directoryPath);
        if (numberOfFilesInDir == 0) {
            LOGGER.debug("skipping directory '" + directoryPath.getFileName() + "' as it is empty");
        } else if (numberOfFilesInDir == 1) {
            processNewImage(directoryPath);
            addToCache(directoryPath);
        } else if (numberOfFilesInDir >= 3) {
            addToCache(directoryPath);
        }
    }

    private void processNewImage(Path directoryPath) {
        LOGGER.info("processing new image in directory '" + directoryPath + "'");
        String[] imageFiles = listSupportedImages(directoryPath);

        File originalFile = renameAndRetrieveOriginalFile(directoryPath, imageFiles[0]);

        resizeTo(SMALL, directoryPath, originalFile, imageFiles[0]);
        resizeTo(BIG, directoryPath, originalFile, imageFiles[0]);
    }

    private void addToCache(Path directoryPath) {
        if (isAlreadyCached(directoryPath)) {
            LOGGER.debug("skipping already cached image '" + directoryPath.getFileName() + "'");
            return;
        }
        String[] imageFiles = listSupportedImages(directoryPath);


        // we will have wrong height and width without the orientation in the original image data, but accept it as we don't use it
        Image originalImage = retrieveImageInfo(ORIGINAL, directoryPath, imageFiles);
        JpegImageMetadata metadata = retrieveJpegMetadata(originalImage.localFile());

        Image smallImage = retrieveImageInfo(SMALL, directoryPath, imageFiles, Integer.valueOf(exifOf(metadata, TIFF_TAG_ORIENTATION)));
        Image bigImage = retrieveImageInfo(BIG, directoryPath, imageFiles, Integer.valueOf(exifOf(metadata, TIFF_TAG_ORIENTATION)));

        // TODO: 9/14/23 fix exif data
        ImageData imageData = new ImageData(
                directoryPath.getFileName().toString(),
                directoryPath.getFileName().toString(),
                originalImage,
                smallImage,
                bigImage,
                dateTimeOf(exifOf(metadata, TIFF_TAG_DATE_TIME)),
                humanReadableOf(exifOf(metadata, TIFF_TAG_DATE_TIME)),
                exifOf(metadata, EXIF_TAG_FOCAL_LENGTH),
                exifOf(metadata, EXIF_TAG_SHUTTER_SPEED_VALUE),
                exifOf(metadata, EXIF_TAG_APERTURE_VALUE),
                exifOf(metadata, EXIF_TAG_ISO)
        );

        LOGGER.info("adding '" + imageData + "' to cache ...");
        this.images.add(imageData);
    }

    private LocalDateTime dateTimeOf(String exifDateTime) {
        return LocalDateTime.parse(exifDateTime, this.exifParser);
    }

    public String humanReadableOf(String exifDateTime) {
        return dateTimeOf(exifDateTime).format(outputFormatter);
    }

    private String[] listSupportedImages(Path directoryPath) {
        return directoryPath.toFile()
                .list((dir, name) -> {
                    String[] dotSplitted = name.split("\\.");
                    String ending = dotSplitted[dotSplitted.length - 1].toUpperCase();

                    return this.supportedFileEndings.contains(ending);
                });
    }

    private boolean isAlreadyCached(Path directoryPath) {
        return this.images.stream().anyMatch(imageData -> Objects.equals(imageData.id(), directoryPath.getFileName().toString()));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void resizeTo(ImageSize imageSize, Path directoryPath, File originalFile, String originalFilename) {
        try {
            // prepare
            BufferedImage originalImage = ImageIO.read(originalFile);
            JpegImageMetadata metadata = retrieveJpegMetadata(originalFile);

            // resize to temp
            BufferedImage resizedImage = Scalr.resize(originalImage, ULTRA_QUALITY, imageSize.targetSize());
            File tempResizedFile = new File(directoryPath + "/temp-" + originalFilename);
            ImageIO.write(resizedImage, "jpeg", tempResizedFile);

            // use temp to write exif to finished file
            OutputStream os = new BufferedOutputStream(new FileOutputStream(directoryPath + "/" + imageSize.filePrefix() + originalFilename));
            new ExifRewriter().updateExifMetadataLossless(tempResizedFile, os, metadata.getExif().getOutputSet());
            tempResizedFile.delete();

            LOGGER.info("resized original file '" + originalFile + "' to '" + imageSize + "'");
        } catch (IOException | ImageReadException | ImageWriteException e) {
            throw new RuntimeException(e);
        }
    }

    private static File renameAndRetrieveOriginalFile(Path directoryPath, String imageFile) {
        File pristineOriginalFile = Paths.get(directoryPath + "/" + imageFile).toFile();
        File originalFile = Paths.get(directoryPath + "/" + ORIGINAL.filePrefix() + imageFile).toFile();
        if (pristineOriginalFile.renameTo(originalFile)) {
            return originalFile;
        } else {
            throw new RuntimeException("failed renaming original file in directory = '" + directoryPath.getFileName() + "'");
        }
    }

    private static long filesCount(Path directoryPath) {
        try (Stream<Path> stream = Files.list(directoryPath)) {
            return stream.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static JpegImageMetadata retrieveJpegMetadata(File imageFile) {
        try {
            return (JpegImageMetadata) Imaging.getMetadata(imageFile);
        } catch (IOException | ImageReadException e) {
            throw new RuntimeException(e);
        }
    }

    private static String exifOf(JpegImageMetadata jpegImageMetadata, TagInfo exifTag) {
        try {
            return jpegImageMetadata.findEXIFValueWithExactMatch(exifTag) != null ? jpegImageMetadata.findEXIFValueWithExactMatch(exifTag).getValue().toString() : "";
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        }
    }

    private static Image retrieveImageInfo(ImageSize imageSize, Path directoryPath, String[] imageFiles) {
        return retrieveImageInfo(imageSize, directoryPath, imageFiles, null);
    }

    private static Image retrieveImageInfo(ImageSize imageSize, Path directoryPath, String[] imageFiles, Integer orientation) {
        Optional<String> fileName = imageSize.matchingPrefix(imageFiles);
        if (fileName.isEmpty()) {
            throw new RuntimeException("cannot find a match for imageSize = '" + imageSize + "' and imageFiles = '" + Arrays.toString(imageFiles) + "'");
        }
        File imageFile = Paths.get(directoryPath.toString(), fileName.get()).toFile();
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // workaround to handle image orientation, see https://www.awaresystems.be/imaging/tiff/tifftags/orientation.html
        List<Integer> knownPortraitOrientations = List.of(6, 8);

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        if (orientation != null && knownPortraitOrientations.contains(orientation)) {
            height = bufferedImage.getWidth();
            width = bufferedImage.getHeight();
        }
        return new Image(
                "/api/v1/images/" + directoryPath.getFileName() + "/" + imageFile.getName(),
                Integer.toString(height),
                Integer.toString(width),
                imageFile
        );
    }

}
