package com.zuehlke.pgadmissions.services.builders.pdf;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPageEventHelper;

public abstract class AbstractModelBuilder extends PdfPageEventHelper {
    
    protected static final float MAX_WIDTH_PERCENTAGE = 100f;
    
    protected static final int NORMAL_FONT_SIZE = 12;
    
    protected static final int SMALL_FONT_SIZE  = 10;
    
    protected static final int SMALLER_FONT_SIZE = 8;
    
    protected static final Font BOLD_FONT = new Font(FontFamily.HELVETICA, NORMAL_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALL_BOLD_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALL_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.NORMAL);
    
    protected static final Font SMALL_GREY_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.NORMAL, BaseColor.LIGHT_GRAY);
    
    protected static final Font SMALLER_BOLD_FONT = new Font(FontFamily.HELVETICA, SMALLER_FONT_SIZE, Font.BOLD);
    
    protected static final Font SMALLER_FONT = new Font(FontFamily.HELVETICA, SMALLER_FONT_SIZE, Font.NORMAL);
    
    protected static final Font LINK_FONT = new Font(FontFamily.HELVETICA, SMALL_FONT_SIZE, Font.UNDERLINE, BaseColor.BLUE);
    
    protected static final BaseColor GRAY_COLOR = new BaseColor(220, 220, 220);

    @Value("${xml.export.provided}")
    protected String provided;
    
    @Value("${xml.export.not.provided}")
    protected String notProvided;
    
    @Value("${xml.export.not.required}")
    protected String notRequired;
    
    @Value("${xml.export.date.format}")
    protected String dateFormat;
    
    @Value("${xml.export.logo.file.location}")
    protected String logoFileLocation;
    
    protected Paragraph addSectionSeparator() {
        return new Paragraph(" ");
    }
    
    protected PdfPCell newTableCell(final String content, final Font font) {
        PdfPCell cell = null;
        if (StringUtils.isNotBlank(content)) {
            cell = new PdfPCell(new Phrase(content, font));
        } else {
            cell = new PdfPCell(new Phrase(notProvided, SMALL_GREY_FONT));
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
            cell = new PdfPCell(new Phrase(notProvided, SMALL_GREY_FONT));
        }
        cell.setPaddingBottom(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
    
}
