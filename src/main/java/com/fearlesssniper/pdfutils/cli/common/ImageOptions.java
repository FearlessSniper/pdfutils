package com.fearlesssniper.pdfutils.cli.common;

import picocli.CommandLine.Option;

/**
 * Two types of images for users to choose from
 */
public class ImageOptions {
    public enum ImageType {
        JPEG(".jpg", "jpg"), PNG(".png", "png");
        private final String fileExtension;
        private final String formatName;
    
        private ImageType(String fileExtensionString, String formatName) {
            this.fileExtension = fileExtensionString;
            this.formatName = formatName;
        }
    
        public String getFileExtension() {
            return this.fileExtension;
        }

        public String getFormatName() {
            return this.formatName;
        }
    }

    // JPEG is used by default
    @Option(
            names = {"--type", "--image-type"},
            description = {
                "The type of the output image.",
                "Valid values: ${COMPLETION-CANDIDATES}"
            },
            defaultValue = "JPEG"
    )
    public ImageType imgType;
}
