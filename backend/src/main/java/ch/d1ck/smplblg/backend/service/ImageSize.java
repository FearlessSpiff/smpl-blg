package ch.d1ck.smplblg.backend.service;

import java.util.Optional;

import static java.util.Arrays.stream;

public enum ImageSize {

    SMALL("small-", 720),
    BIG("big-", 2160),
    ORIGINAL("original-", 0);

    private final String filePrefix;
    private final int targetSize;

    // private enum constructor
    ImageSize(String filePrefix, int targetSize) {
        this.filePrefix = filePrefix;
        this.targetSize = targetSize;
    }

    public String filePrefix() {
        return this.filePrefix;
    }
    public int targetSize() {
        return this.targetSize;
    }

    public Optional<String> matchingPrefix(String[] fileList) {
        return stream(fileList).filter(fileName -> fileName.startsWith(this.filePrefix)).findFirst();
    }

}
