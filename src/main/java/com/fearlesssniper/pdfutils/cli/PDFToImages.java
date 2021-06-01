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
package com.fearlesssniper.pdfutils.cli;

import com.fearlesssniper.pdfutils.cli.common.PDFParameter;
import com.fearlesssniper.pdfutils.cli.common.ImageOptions;
import com.fearlesssniper.pdfutils.cli.common.PDFImageResolution;
import com.fearlesssniper.pdfutils.util.PDDocExtra;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Renders the pages of the PDF document into images.
 * @author fearlesssniper
 */
@Command(
        name = "to-image",
        description = "Convert the pages of the PDF to images."
)
public class PDFToImages implements Callable<Integer> {

    @Mixin
    private PDFImageResolution imageResolution;

    @Option(
            names = {"--prefix"},
            description = "The output prefix of the image files."
    )
    private String filenamePrefix;

    // Allows users to specify what type of images to export
    @Mixin
    private ImageOptions imageOptions;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private PDFParameter pdfArgs;

    private String getFileBaseName() {
        if (this.filenamePrefix == null) {
            // Use name of PDF file
            return FilenameUtils.removeExtension(this.pdfArgs.docFile.getName());
        } else {
            return filenamePrefix;
        }
    }

    @Override
    public Integer call() throws IOException {
        try (PDDocExtra doc = new PDDocExtra(
                PDDocExtra.load(this.pdfArgs.docFile, this.pdfArgs.docPass))) {
            var pagesImage = doc.getPagesImage(this.imageResolution.dpi);
            String fileBaseName = this.getFileBaseName();
            for (int i = 0; i < pagesImage.length; i++) {
                // PREFIX_i.(jpg/png), e.g. pdf_0.jpg
                var outputFile = new File(
                        fileBaseName + "_" + i + this.imageOptions.imgType.getFileExtension());
                ImageIO.write(
                        pagesImage[i], this.imageOptions.imgType.toString(), outputFile);
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new PDFToImages()).execute(args));
    }
}
