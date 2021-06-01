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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Callable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;

/**
 * Command to retrieve metadata from a PDF document.
 * @author fearlesssniper
 */
@Command (
    name = "info", description = "Retrieves info from a PDF document.",
    mixinStandardHelpOptions = true
)
public class PDFInfo implements Callable<Integer> {

    // TODO: Write to JSON file instead of doing nothing
//    @Option(names = {"-j", "--json"},
//            description = "Writes the output into a JSON file.")
//    boolean outJson;

    @ArgGroup(exclusive = false, multiplicity = "1")
    private PDFParameter docArgs;

    /**
     * Format a Calendar value to a String with current locale.
     * 
     * If cal is null, returns "null"
     * @param cal The calendar value to be formatted
     * @return The formatted datetime string
     */
    private static String getFormat(Calendar cal) {
        if (cal == null) return "null";
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            return simpleDateFormat.format(cal.getTime());
        }
    }

    @Override
    public Integer call() {
        try (PDDocument doc = PDDocument.load(docArgs.docFile,
                                              docArgs.docPass)) {
            PDDocumentInformation info = doc.getDocumentInformation();
            // Print the metadata of the PDF
            System.out.println("filename: " + docArgs.docFile.getName());
            System.out.println("Author: " + info.getAuthor());
            System.out.println("Creator: " + info.getCreator());
            Calendar modDate = info.getModificationDate();
            Calendar creationDate = info.getCreationDate();
            System.out.println("Modification Date: "
                    + PDFInfo.getFormat(modDate));
            System.out.println("Creation Date: "
                    + PDFInfo.getFormat(creationDate));
            System.out.println("Title: " + info.getTitle());
            System.out.println("Subject: " + info.getSubject());
            System.out.println("Keywords: " + info.getKeywords());
        }
        catch (InvalidPasswordException e) {
            System.err.println(
                "The PDF document is encrypted. You might have " +
                "entered a wrong password or did not specify one.\nPlease " +
                "specify one with the -p option preceding the file name.");
            return -1;
        }
        catch (IOException iOException) {
            System.err.println("Something went wrong while reading file: "
                    + iOException.getLocalizedMessage());
            return 5;
        }
        return 0;
    }
    
}
