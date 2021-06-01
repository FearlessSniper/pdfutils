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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeaderFooter;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPresetGeometry2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTransform2D;
import org.openxmlformats.schemas.drawingml.x2006.main.STShapeType;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromH;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STRelFromV;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;

/**
 * Methods for adding content to a run
 * 
 * This class was intended to try to add images to the Word document by anchoring
 * mode. However, the problem turned out to not be in anchor vs inline, so this
 * class is not used.
 * 
 * @deprecated Not used by any code
 * @author fearlesssniper
 */
@Deprecated
public class XWPFRunExtra {

    // The internal representation of a run
    private final XWPFRun run;
    private final CTR ctr;
    private static int currentId = 399;

    public XWPFRunExtra(XWPFRun run) {
        this.run = run;
        this.ctr = run.getCTR();
    }

    private static int getNewId() {
        currentId++;
        return currentId;
    }

    public XWPFPicture addAnchoredPicture(
            InputStream pictureData, int pictureType, String filename,
            int width, int height)
            throws InvalidFormatException, IOException, XmlException {
        // Add the picture to the document;
        // From original XWPFRun
        var parent = this.run.getParent();
        String relationId;
        XWPFPictureData picData;
        if (parent.getPart() instanceof XWPFHeaderFooter) {
            XWPFHeaderFooter headerFooter = (XWPFHeaderFooter) parent.getPart();
            relationId = headerFooter.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData) headerFooter.getRelationById(relationId);
        } else {
            @SuppressWarnings("resource")
            XWPFDocument doc = parent.getDocument();
            relationId = doc.addPictureData(pictureData, pictureType);
            picData = (XWPFPictureData) doc.getRelationById(relationId);
        }

        // Actually doing the drawing stuff
        var drawing = this.ctr.addNewDrawing();
        var anchor = drawing.addNewAnchor();
        // Set anchor no minimum distance
        anchor.setDistT(0);
        anchor.setDistB(0);
        anchor.setDistL(0);
        anchor.setDistR(0);
        anchor.setSimplePos2(false);
        // Set positioning
        // Placement: Stick to top corner
        var posH = anchor.addNewPositionH();
        posH.setRelativeFrom(STRelFromH.PAGE);
        posH.setPosOffset(0);
        var posV = anchor.addNewPositionV();
        posV.setRelativeFrom(STRelFromV.PAGE);
        posV.setPosOffset(0);
        // The size of the image
        var extent = anchor.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);
        int id = XWPFRunExtra.getNewId();
        var properties = anchor.addNewDocPr();
        properties.setId(id);
        properties.setName(filename);
        // Maybe I'll do the whole graphic thing in bare xml
        String graphicXmlString
                = "<a:graphic xmlns:a=\"" + CTGraphicalObject.type.getName().getNamespaceURI() + "\">"
                + "<a:graphicData uri=\"" + org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture.type.getName().getNamespaceURI() + "\">"
                + "<pic:pic xmlns:pic=\"" + org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture.type.getName().getNamespaceURI() + "\" />"
                + "</a:graphicData>"
                + "</a:graphic>";
        var graphic = CTGraphicalObject.Factory.parse(graphicXmlString);
        var graphicData = graphic.getGraphicData();
        // Get the first pic children element
        var pic = (CTPicture)(graphicData.selectChildren(CTPicture.type.getName())[0]);
        var nvPicPr = pic.addNewNvPicPr();
        var cNvPicPr = nvPicPr.addNewCNvPicPr();
        cNvPicPr.addNewPicLocks().setNoChangeAspect(true);
        var blipFillProperties = pic.addNewBlipFill();
        var blip = blipFillProperties.addNewBlip();
        blip.setEmbed(parent.getPart().getRelationId(picData));
        blipFillProperties.addNewStretch().addNewFillRect();
        CTShapeProperties spPr = pic.addNewSpPr();
        CTTransform2D xfrm = spPr.addNewXfrm();

        CTPoint2D off = xfrm.addNewOff();
        off.setX(0);
        off.setY(0);

        CTPositiveSize2D ext = xfrm.addNewExt();
        ext.setCx(width);
        ext.setCy(height);

        CTPresetGeometry2D prstGeom = spPr.addNewPrstGeom();
        prstGeom.setPrst(STShapeType.RECT);
        prstGeom.addNewAvLst();
        anchor.setGraphic(graphic);
        var xwpfPicture = new XWPFPicture(pic, run);
        return xwpfPicture;
    }
    public XWPFPicture addAnchoredPicture2(
            InputStream pictureData, int pictureType, String filename,
            int width, int height) throws InvalidFormatException, IOException {
        // We want to graphics object in the inline;
        // Scrap others
        var picture = this.run.addPicture(
                pictureData, pictureType, filename, width, height);
        // Retrieve the graphics object added by `addPicture`
        var graphic = this.ctr.getDrawingArray(0).getInlineArray(0).getGraphic();
        // Set up the anchor
        var drawing = this.ctr.addNewDrawing();
        var anchor = drawing.addNewAnchor();
        // Set anchor no minimum distance
        anchor.setDistT(0);
        anchor.setDistB(0);
        anchor.setDistL(0);
        anchor.setDistR(0);
        // Sets all the properties in the anchor. Probably not the best way
        anchor.setAllowOverlap(false);
        anchor.setRelativeHeight(0);
        anchor.setLocked(false);
        anchor.setSimplePos2(false);
        anchor.setLayoutInCell(true);
        anchor.setBehindDoc(false);
        // Try adding new simple position even if we don't use it
        var simplePos = anchor.addNewSimplePos();
        simplePos.setX(0);
        simplePos.setY(0);
        // Set positioning
        // Placement: Stick to top corner
        var posH = anchor.addNewPositionH();
        posH.setRelativeFrom(STRelFromH.PAGE);
        posH.setPosOffset(0);
        var posV = anchor.addNewPositionV();
        posV.setRelativeFrom(STRelFromV.PAGE);
        posV.setPosOffset(0);
        // The size of the image
        var extent = anchor.addNewExtent();
        extent.setCx(width);
        extent.setCy(height);
        var effectExtent = anchor.addNewEffectExtent();
        effectExtent.setT(0);
        effectExtent.setB(0);
        effectExtent.setL(0);
        effectExtent.setR(0);
        anchor.setDistT(0);
        anchor.setDistB(0);
        anchor.setDistL(0);
        anchor.setDistR(0);
        anchor.addNewWrapTopAndBottom();
        int id = this.getNewId();
        var properties = anchor.addNewDocPr();
        properties.setId(id);
        properties.setName(filename);
        anchor.addNewCNvGraphicFramePr();
        // Set the image the `addPicture` made to our anchor
        anchor.setGraphic(graphic);
        // Remove the inline drawing
        this.ctr.removeDrawing(0);
        // I guess i'll just return the picture from the original function
        // No idea whether the picture will have an invalid reference or not
        return picture;
    }
    
    /**
     * @deprecated not used anymore
     */
    @Deprecated
    public static void main(String[] args) throws IOException {
        File imageFile = new File(args[0]);
        byte[] byteArray = Files.readAllBytes(imageFile.toPath());
        var imageInStream = new ByteArrayInputStream(byteArray);
        var bufImg = ImageIO.read(imageFile);
        var xwpfDoc = new XWPFDocument();
        var run = xwpfDoc.createParagraph().createRun();
        var runExtra = new XWPFRunExtra(run);
        bufImg.getWidth();
    }
}
