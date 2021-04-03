package com.fearlesssniper.pdfutils.cli;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import com.fearlesssniper.pdfutils.util.PDDocExtra;

import org.apache.pdfbox.pdmodel.PDDocument;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "merge", mixinStandardHelpOptions = true,
         description = "Merges a number of PDFs into a new PDF.",
         version = "1.0")
public class PDFMerge implements Callable<Integer> {
    @Option(names = {"-o", "--output"}, description = "The output PDF", required = true)
    private File outputFile;

    @Parameters(paramLabel = "PDF", description = "PDF documents to be merged")
    private File[] docFiles;

    @Override
    public Integer call() throws IOException {
        // Note: The source document must not be closed when
        // saving the result document.
        try (PDDocExtra resultDoc = new PDDocExtra()) {
            PDDocument[] docs = new PDDocument[this.docFiles.length];
            // Load each document
            for (int i = 0; i < this.docFiles.length; i++) {
                docs[i] = PDDocument.load(this.docFiles[i]);
            }
            for (var doc: docs) {
                resultDoc.appendDoc(doc);
            }
            resultDoc.save(this.outputFile);
            for (var doc: docs) {
                doc.close();
            }
        }
        return 0;
    }

//    public static void main(String[] args) {
//        System.exit(new CommandLine(new PDFMerge()).execute(args));
//    }
}
