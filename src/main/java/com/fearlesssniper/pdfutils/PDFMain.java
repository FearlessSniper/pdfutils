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
package com.fearlesssniper.pdfutils;

import com.fearlesssniper.pdfutils.cli.PDFEncrypt;
import com.fearlesssniper.pdfutils.cli.PDFInfo;
import com.fearlesssniper.pdfutils.cli.PDFMerge;
import com.fearlesssniper.pdfutils.cli.PDFRemoveEncrypt;
import com.fearlesssniper.pdfutils.cli.PDFReorder;
import com.fearlesssniper.pdfutils.cli.PDFRotate;
import com.fearlesssniper.pdfutils.cli.PDFToImages;
import com.fearlesssniper.pdfutils.cli.PDFToWord;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main command
 *
 * @author fearlesssniper
 */
@Command(
        name = "pdfutils",
        description = "A collection of utilities for modifying PDF documents.",
        versionProvider = AppVersion.class,
        synopsisSubcommandLabel = "COMMAND",
        mixinStandardHelpOptions = true,
        subcommands = {
            PDFMerge.class,
            PDFEncrypt.class,
            PDFRemoveEncrypt.class,
            PDFToWord.class,
            PDFInfo.class,
            PDFToImages.class,
            PDFReorder.class,
            PDFRotate.class,
        }
)
public class PDFMain {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PDFMain()).execute(args);
        System.exit(exitCode);
    }
}
