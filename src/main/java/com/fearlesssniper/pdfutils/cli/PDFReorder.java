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
import com.fearlesssniper.pdfutils.util.SequenceGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import picocli.CommandLine;
import picocli.CommandLine.Option;

/**
 * Reorder the pages in the ordering specified
 *
 * @author fearlesssniper
 */
@CommandLine.Command(name = "reorder", description = "Reorder the pages of the PDF document"
        + " in the order specified.")
public class PDFReorder implements Callable<Integer> {

    private static class OrderingOptions {
        // The names are not very descriptive; needs improvement
        @CommandLine.Option(names = { "--by-position" }, description = {
                "Specify the final order by the page's position in the", "original document.",
                "e.g. If the document is now in 1, 3, 5, 2, 4 original",
                "order, then the second page of the final document", "should the fourth page of the original document;",
                "the third page of the final document should be the",
                "second page of the document, etc. Hence the order by",
                " position should be 1, 4, 2, 5, 3" }, arity = "1..*", required = true)
        public List<Integer> orderByPosition;

        @CommandLine.Option(names = { "--order", "--ordering", "--by-original-position" }, description = {
                "Specify the order by the original position of the page.",
                "e.g. A document with 4 pages have the ordering mixed up,",
                "the second page is the third page, the third page is in",
                "second. In that case, specify '1 3 2 4' to arrange it",
                "back to correct order.", }, arity = "1..*", required = true)
        public List<Integer> orderByOriginalPosition;

        @Option(names = { "-S", "--seq-string", "--op-string", }, description = {
                "Specify the order by the original postion of the pages,",
                "but the expression is evaluated. Keywords `odd` and `even`",
                "will expand to the odd numbered pages and even numbered pages",
                "respectively. Using `reverse` before `odd` or `even` reverses the", "order of the sequence.",
                "Use a space or comma, or both (i.e. \' \' or \',\')",
                "to seperate the numbers. Quote the sequence with double quotes.",
                "e.g. \"odd even\", \"odd reverse even\"", }, required = true)
        public String originalPostionString;

        @Option(names = { "-s", "--pos-string", }, description = {
                "Same as original postion string, but the numbers represent the",
                "page index of the current document." }, required = true)
        public String positionString;

    }

    @CommandLine.ArgGroup(exclusive = true, multiplicity = "1")
    private OrderingOptions orderingGroup;

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
        } else {
            return new File(FilenameUtils.removeExtension(this.pdfParam.docFile.getName()) + suffix);
        }
    }

    @Override
    public Integer call() throws Exception {
        try (var doc = PDDocument.load(pdfParam.docFile, pdfParam.docPass)) {
            try (var newDoc = new PDDocument()) {
                var oldPageTree = doc.getPages();
                var newPageTree = newDoc.getPages();
                // If the user specified it
                // An error is triggered when original position and
                // position is both set; Also triggered when none is specified
                // Done by picocli

                // If using ordering string mode, put the parsed list into
                // orderByOriginalPostion
                // / orderByPosition
                var sequenceGenerator = new SequenceGenerator(oldPageTree.getCount());
                if (orderingGroup.originalPostionString != null) {
                    orderingGroup.orderByOriginalPosition = sequenceGenerator
                            .getPageSequence(orderingGroup.originalPostionString);
                } else if (orderingGroup.positionString != null) {
                    orderingGroup.orderByPosition = sequenceGenerator.getPageSequence(orderingGroup.positionString);
                }

                if (orderingGroup.orderByOriginalPosition != null) {
                    orderingGroup.orderByPosition = new ArrayList<>();
                    for (int i = 0; i < orderingGroup.orderByOriginalPosition.size(); i++) {
                        orderingGroup.orderByPosition.add(orderingGroup.orderByOriginalPosition.indexOf(i + 1) + 1);
                    }
                }
                // So ultimately despite having four options to provide the order
                // the other three is converted into the 'order by positon' format
                for (var i : orderingGroup.orderByPosition) {
                    newPageTree.add(oldPageTree.get(i - 1));
                }
                newDoc.save(getActualOutputFile("_reordered.pdf"));
            }
        }
        return 0;
    }
}
