/*
 * The MIT License
 *
 * Copyright 2021 fearlesssniper.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
