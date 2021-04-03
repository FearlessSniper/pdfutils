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
package com.fearlesssniper.pdfutils.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Contains extra methods for manipulating PDF documents
 *
 * @author fearlesssniper
 */
public class PDDocExtra extends PDDocument {

    private final PDPageTree currentDocTree;
    private final PDFRenderer renderer;

    public PDDocExtra() {
        super();
        currentDocTree = super.getPages();
        renderer = new PDFRenderer(this);
    }

    public PDDocExtra(PDDocument doc) {
        super(doc.getDocument());
        currentDocTree = super.getPages();
        renderer = new PDFRenderer(this);
    }

    /**
     * Appends another document at the end of the current file.
     *
     * @param doc The target document.
     */
    public void appendDoc(PDDocument doc) {
        PDPageTree targetDocTree = doc.getPages();
        for (PDPage page : targetDocTree) {
            this.currentDocTree.add(page);
        }
    }

    /**
     * Rotates the page at the given index by rotation.
     *
     * @param index The index of the page
     * @param rotation Rotation angle in degrees
     */
    public void rotatePage(int index, int rotation) {
        PDPage page = this.getPage(index);
        page.setRotation(rotation);
    }

    /**
     * Returns a brand new reordered document.
     *
     * @param ordering The ordering of the pages, zero-indexed.
     * @return The new reordered PDDocument
     */
    public PDDocument getReorderedDoc(int[] ordering) {
        PDDocument newDoc = new PDDocument();
        PDPageTree newDocTree = newDoc.getPages();
        for (int i : ordering) {
            // The apache documentation did not specify what
            // will happen if i is out-of-bound;
            // I'll leave it.
            newDocTree.add(currentDocTree.get(i));
        }
        return newDoc;
    }

    /**
     * Returns the rendered RGB image of the page of the given index.
     *
     * @param index The index of the page to be rendered
     * @param dpi Resolution in dpi
     * @return The image.
     * @throws IOException Thrown by PDFRenderer
     */
    public BufferedImage getPageImage(int index, float dpi) throws IOException {
        return this.renderer.renderImageWithDPI(index, dpi);
    }

    /**
     * Returns the rendered RGB images of all PDF pages.
     *
     * @param dpi Resolution in dpi
     * @return The images of the pages
     * @throws IOException Thrown by PDFRenderer
     */
    public BufferedImage[] getPagesImage(float dpi) throws IOException {
        BufferedImage[] pageImages = new BufferedImage[this.getNumberOfPages()];
        for (int i = 0; i < this.getNumberOfPages(); i++) {
            pageImages[i] = this.renderer.renderImageWithDPI(i, dpi);
        }
        return pageImages;
    }
}
