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
import com.fearlesssniper.pdfutils.util.PDPageExtra;
import com.fearlesssniper.pdfutils.util.XWPFDocumentExtra;
import com.fearlesssniper.pdfutils.util.XWPFParagraphExtra;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.xmlbeans.XmlException;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Converts a PDF document to Word file by rendering images of the PDF and
 * insert into the Word document.
 *
 * @author fearlesssniper
 */
@Command(
        name = "to-word",
        description = "Converts a PDF document to Word document by images.",
        showDefaultValues = true
)
public class PDFToWord implements Callable<Integer> {

    @Option(
            names = {"-o", "--output"},
            description = {
                "The output Word document",
                "If the output document is not given,"
                + " the Word document will be named as the given PDF."
            }
    )
    private File outputWord;

    @Mixin
    private PDFImageResolution imageResolution;
    
    private enum PageSizes {
        A0(PDRectangle.A0), A1(PDRectangle.A1), A2(PDRectangle.A2),
        A3(PDRectangle.A3), A4(PDRectangle.A4), A5(PDRectangle.A5),
        A6(PDRectangle.A6), LEGAL(PDRectangle.LEGAL),
        LETTER(PDRectangle.LETTER);
        private final PDRectangle rectangleSize;

        private PageSizes(PDRectangle rectangleSize) {
            this.rectangleSize = rectangleSize;
        }

        public PDRectangle getRectangleSize() {
            return this.rectangleSize;
        }
    }
    @Option(
            names = {"-s", "--size"},
            description = {
                "Force the document size into a standard paper size",
                "Instead of using the size of the PDF.",
                "Supported sizes: ${COMPLETION-CANDIDATES}",
            }
    )
    private PageSizes specifiedSize;

    // Allows users to specify what type of images to be embedded
    // in the Word document
    @Mixin
    private ImageOptions imageOptions;
    
    @ArgGroup(exclusive = false, multiplicity = "1")
    private PDFParameter pdfArgs;

    @Override
    public Integer call() throws IOException, InvalidFormatException, XmlException {
        // Draw images of PDF into images
        // TODO: Did not handle when user gives invalid password
        try (PDDocExtra pdfDoc = new PDDocExtra(
                PDDocument.load(pdfArgs.docFile, pdfArgs.docPass))) {
            BufferedImage[] pagesImages = pdfDoc.getPagesImage(
                    this.imageResolution.dpi);
            try (XWPFDocumentExtra wordDoc = new XWPFDocumentExtra()) {
                for (int i = 0; i < pdfDoc.getNumberOfPages(); i++) {
                    // Dimensions in points
                    PDPageExtra.Dimensions wordPageDimensions;
                    if (this.specifiedSize != null) {
                        // Use specifed size instead of PDF page size
                        wordPageDimensions = PDPageExtra.getPageDimensions(
                                this.specifiedSize.getRectangleSize());
                    } else {
                        wordPageDimensions = PDPageExtra.getPageDimensions(
                                pdfDoc.getPage(i));
                    }

                    // Create a paragraph for each page
                    var paragraph = wordDoc.createParagraph();

                    var run = paragraph.createRun();
                    // Process the pictures
                    var byteStream = new ByteArrayOutputStream();
                    ImageIO.write(pagesImages[i], this.imageOptions.imgType.getFormatName(), byteStream);
                    var byteArrayInputStream
                            = new ByteArrayInputStream(byteStream.toByteArray());
                    int imageType = Document.PICTURE_TYPE_JPEG; // Image type as specified by XWPF documents
                    if (this.imageOptions.imgType == ImageOptions.ImageType.JPEG) {
                        imageType = Document.PICTURE_TYPE_JPEG;
                    } else if (this.imageOptions.imgType == ImageOptions.ImageType.PNG) {
                        imageType = Document.PICTURE_TYPE_PNG;
                    }
                    run.addPicture(byteArrayInputStream, imageType,
                            "page" + i,
                            Units.toEMU(wordPageDimensions.width),
                            Units.toEMU(wordPageDimensions.height));
                    // Inserting image in anchor mode
//                    XWPFRunExtra myRunExtra = new XWPFRunExtra(run);
//                    myRunExtra.addAnchoredPicture2(
//                            byteArrayInputStream,
//                            Document.PICTURE_TYPE_PNG,
//                            "page" + i,
//                            Units.toEMU(pageDimensions.width),
//                            Units.toEMU(pageDimensions.height));
                    // Specify the section properties (page size, etc.)
                    // after the actual content
                    // Create a paragraph for the section properties
                    // including page size, margin, etc.
                    if (i == pdfDoc.getNumberOfPages() - 1) {
                        // If it is the last page, set the page size in a
                        // sectPr under w:body instead of creating a new 
                        // paragraph
                        // Multiply by 20 to convert from points to twips
                        wordDoc.setPagesSize(
                                wordPageDimensions.width * 20,
                                wordPageDimensions.height * 20
                        );
                        wordDoc.setDocumentMargin(0, 0, 0, 0);
                    } else {
                        var pagePropertiesParagraph = wordDoc.createParagraph();
                        var myParagraph = new XWPFParagraphExtra(
                                pagePropertiesParagraph);
                        // Convert from points to twips; 1 point = 20 twips
                        myParagraph.setPageSize(
                                wordPageDimensions.width * 20,
                                wordPageDimensions.height * 20
                        );
                        myParagraph.setMargin(0, 0, 0, 0);
                    }
                }
                FileOutputStream outFileStream;
                if (this.outputWord == null) {
                    // Use original file name
                    outFileStream = new FileOutputStream(
                            FilenameUtils.removeExtension(
                                    this.pdfArgs.docFile.getName()) + ".docx");
                } else {
                    outFileStream = new FileOutputStream(this.outputWord);
                }
                wordDoc.write(outFileStream);
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new PDFToWord()).execute(args));
    }
}
