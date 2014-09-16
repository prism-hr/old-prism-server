package com.zuehlke.pgadmissions.pdf;

import org.apache.commons.lang.StringUtils;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

public abstract class AbstractPdfModelBuilder extends PdfStyling {

    protected static final String NOT_PROVIDED = "Not Provided";

    protected static final String PROVIDED = "Provided";
    
    protected Paragraph addSectionSeparators() {
        return new Paragraph(" ");
    }
    
    protected PdfPCell newTableCell(final String content, final Font font) {
        PdfPCell cell = null;
        if (StringUtils.isNotBlank(content)) {
            cell = new PdfPCell(new Phrase(content, font));
        } else {
            cell = new PdfPCell(new Phrase(NOT_PROVIDED, SMALL_GREY_FONT));
        }
        cell.setPaddingBottom(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }

    protected PdfPCell newGrayTableCell(final String content, final Font font) {
        PdfPCell c1 = newTableCell(content, font);
        c1.setBackgroundColor(GRAY_COLOR);
        return c1;
    }

    protected PdfPCell newTableCell(final String content, final Font font, final Integer appendixNumber) {
        PdfPCell cell = null;
        if (StringUtils.isNotBlank(content)) {
            cell = new PdfPCell(new Phrase(new Chunk(content, font).setLocalGoto(appendixNumber.toString())));
        } else {
            cell = new PdfPCell(new Phrase(NOT_PROVIDED, SMALL_GREY_FONT));
        }
        cell.setPaddingBottom(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
    
}
