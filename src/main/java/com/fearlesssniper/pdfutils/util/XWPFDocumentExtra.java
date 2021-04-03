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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

/**
 *
 * @author fearlesssniper
 */
public class XWPFDocumentExtra extends XWPFDocument {

    private CTDocument1 doc;
    private CTBody docBody;
    private CTSectPr bodyPr; // Section properties for body

    public static class A4 {
        public static final int HEIGHT = 16838;
        public static final int WIDTH = 11906;
    }

    public XWPFDocumentExtra() {
        super();
        initVars();
    }

    public XWPFDocumentExtra(File f) throws FileNotFoundException,
            IOException {
        super(new FileInputStream(f));
        initVars();
    }

    public XWPFDocumentExtra(String filePath) throws FileNotFoundException,
            IOException {
        super(new FileInputStream(filePath));
        initVars();
    }

    private void initVars() {
        this.doc = this.getDocument();
        if (this.doc.isSetBody()) {
            this.docBody = this.doc.getBody();
        } else {
            this.docBody = this.doc.addNewBody();
        }
        if (this.docBody.isSetSectPr()) {
            this.bodyPr = this.docBody.getSectPr();
        } else {
            this.bodyPr = this.docBody.addNewSectPr();
        }
    }

    /**
     * Sets the margins of the document that applies to the whole document.
     * The units of the margins are in twips, which is 1/1440 inch, and
     * 1/20 point.
     * (1 inch = 72 point = 1440 twip)
     *
     * @param left Left margin in twips.
     * @param right Right margin in twips.
     * @param top Top margin in twips.
     * @param bottom Bottom margin in twips.
     */
    public void setDocumentMargin(int left, int right, int top, int bottom) {
        CTPageMar mar;
        if(this.bodyPr.isSetPgMar()) {
            mar = this.bodyPr.getPgMar();
        } else {
            mar = this.bodyPr.addNewPgMar();
        }
        mar.setLeft(left);
        mar.setRight(right);
        mar.setTop(top);
        mar.setBottom(bottom);
    }

    /**
     * Set the size of the pages in the documents that applies to the whole
     * document.
     * The units of the lengths are in twips.
     *
     * @param width Width in twips.
     * @param height Height in twips.
     */
    public void setPagesSize(float width, float height) {
        CTPageSz pageSz;
        if (this.bodyPr.isSetPgSz()) {
            pageSz = this.bodyPr.getPgSz();
        } else {
            pageSz = this.bodyPr.addNewPgSz();
        }
        pageSz.setW(width);
        pageSz.setH(height);
    }

}
