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
import java.io.File;
import java.util.concurrent.Callable;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Removes encyrption from the PDF document. Note that this requires the
 * password protecting the document to be known, this command is not a password
 * cracking tool.
 *
 * @author fearlesssniper
 */
@Command(
        name = "rm-encrypt",
        description = "Removes the password encryption from a PDF document"
)
public class PDFRemoveEncrypt implements Callable<Integer> {

    @CommandLine.Mixin
    private PDFOutput outFileGroup;

    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1")
    private PDFParameter pdfParam;

    // TODO: Take out this common method of PDFRotate, PDFRemoveEncrypt,
    // PDFEncrypt, etc.
    /**
     * If the file in pdfArgs is null, then use the original file with the infix
     *
     * @param suffix The suffix after the basename
     * @return The file to write to
     */
    private File getActualOutputFile(String suffix) {
        if (outFileGroup.outputFile != null) {
            return pdfParam.docFile;
        }
        else {
            return new File(
                    FilenameUtils.removeExtension(this.pdfParam.docFile.getName())
                    + suffix);
        }
    }

    @Override
    public Integer call() throws Exception {
        try (var doc = PDDocument.load(pdfParam.docFile, pdfParam.docPass)) {
            doc.setAllSecurityToBeRemoved(true);
            doc.save(getActualOutputFile("_encryption_removed.pdf"));
        }
        return 0;
    }
}
