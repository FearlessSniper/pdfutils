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

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

/**
 *
 * @author fearlesssniper
 */
public class XWPFParagraphExtra {
    private final XWPFParagraph paragraph;
    private final CTP paragraphCtp;
    private final CTPPr paragraphPPr; // Paragraph property
    private final CTSectPr paragraphSectPr;

    public XWPFParagraphExtra(XWPFParagraph paragraph) {
        this.paragraph = paragraph;
        this.paragraphCtp = this.paragraph.getCTP();
        if (this.paragraphCtp.isSetPPr()) {
            this.paragraphPPr = this.paragraphCtp.getPPr();
        }
        else {
            this.paragraphPPr = this.paragraphCtp.addNewPPr();
        }
        if (this.paragraphPPr.isSetSectPr()) {
            this.paragraphSectPr = this.paragraphPPr.getSectPr();
        } else {
            this.paragraphSectPr = this.paragraphPPr.addNewSectPr();
        }
    }

    /**
     * Set the page size for a paragraph.
     * @param width The width of the page in twips
     * @param height The height of the page in twips
     */
    public void setPageSize(float width, float height) {
        CTPageSz pageSize;
        if (this.paragraphSectPr.isSetPgSz()) {
            pageSize = this.paragraphSectPr.getPgSz();
        }
        else {
            pageSize = this.paragraphSectPr.addNewPgSz();
        }
        pageSize.setW(width);
        pageSize.setH(height);
    }

    /**
     * Set the margin for the page of the paragraph.
     * @param left Left margin in twips.
     * @param right Right margin in twips.
     * @param top Top margin in twips.
     * @param bottom Bottom margin in twips.
     */
    public void setMargin(int left, int right, int top, int bottom) {
        CTPageMar mar;
        if(this.paragraphSectPr.isSetPgMar()) {
            mar = this.paragraphSectPr.getPgMar();
        } else {
            mar = this.paragraphSectPr.addNewPgMar();
        }
        mar.setLeft(left);
        mar.setRight(right);
        mar.setTop(top);
        mar.setBottom(bottom);
    }
}
