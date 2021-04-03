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

import com.fearlesssniper.pdfutils.cli.common.PDFOutput;
import com.fearlesssniper.pdfutils.cli.common.PDFParameter;
import com.fearlesssniper.pdfutils.util.NumberGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Mixin;

/**
 * Rotates specified pages in the PDF
 *
 * @author fearlesssniper
 */
@Command(
        name = "rotate",
        description = "Rotates the specified pages"
)
public class PDFRotate implements Callable<Integer> {

    @Option(
            names = {"--odd", "--odd-numbers"},
            description = "Rotate all odd numbered pages."
    )
    private boolean oddNumbers;

    @Option(
            names = {"--even", "--even-numbers"},
            description = "Rotate all even numbered pages."
    )
    private boolean evenNumbers;

    @Option(
            names = {"-a", "--all"},
            description = "Rotate all pages in the document."
    )
    private boolean allPages;

    @Option(
            names = {"-i", "--indexes"},
            description = {
                "The indexes of the pages to rotate.",
                "(Indexes starting from 1)",
            }
    )
    private List<Integer> indexes = new ArrayList<>(); // Default: Empty list

    @Option(
            names = {"-r", "--rotation"},
            description = "The rotation of pages in degrees.",
            required = true
    )
    private int rotation;

    @Mixin
    private PDFOutput outputOption;

    @Mixin
    private PDFParameter pdfArgs;

    /**
     * If the file in pdfArgs is null, then use the original file with the infix
     *
     * @param suffix The suffix after the basename
     * @return The file to write to
     */
    private File getActualOutputFile(String suffix) {
        if (outputOption.outputFile != null) {
            return pdfArgs.docFile;
        } else {
            return new File(
                    FilenameUtils.removeExtension(this.pdfArgs.docFile.getName())
                    + suffix);
        }
    }

    @Override
    public Integer call() throws Exception {
        try (PDDocument doc = PDDocument.load(pdfArgs.docFile, pdfArgs.docPass)) {
            // Make list of pages to rotate
            var rotatePages = new ArrayList<Integer>(); // The index of the pages to be rotated
            if (allPages || (oddNumbers && evenNumbers)) {
                rotatePages.addAll(
                        NumberGenerator.getAllNumbers(doc.getNumberOfPages()));
            } else {
                if (oddNumbers) {
                    rotatePages.addAll(
                            NumberGenerator.getOddNumbers(doc.getNumberOfPages()));
                }
                if (evenNumbers) {
                    rotatePages.addAll(
                            NumberGenerator.getEvenNumbers(doc.getNumberOfPages()));
                }
                // Input validation
//                for (int i: indexes) {
//                    if (i >= doc.getNumberOfPages()) {
//                    }
//                }
                rotatePages.addAll(indexes);
            }
            for (var n : rotatePages) {
                doc.getPage(n - 1).setRotation(rotation);
            }
            doc.save(this.getActualOutputFile("_rotated.pdf"));
        }
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new PDFRotate()).execute(args));
    }
}
