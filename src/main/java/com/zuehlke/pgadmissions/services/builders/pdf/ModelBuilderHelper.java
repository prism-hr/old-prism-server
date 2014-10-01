package com.zuehlke.pgadmissions.services.builders.pdf;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

@Component
public class ModelBuilderHelper {

    @Value("${xml.export.not.provided}")
    public String notProvided;
    
    public Paragraph newSectionSeparator() {
        return new Paragraph(" ");
    }
    
    public PdfPCell newTableCell(final String content, final Font font) {
        return newAppendixTableCell(content, font, null);
    }

    public PdfPCell newColoredTableCell(final String content, final Font font) {
        PdfPCell c1 = newTableCell(content, font);
        c1.setBackgroundColor(ModelBuilderConfiguration.GREY_COLOR);
        return c1;
    }
    
    public PdfPCell newAppendixTableCell(final String content, final Font font, final Integer appendixNumber) {
        PdfPCell cell = null;
        if (StringUtils.isNotBlank(content)) {
            if (appendixNumber == null) {
                cell = new PdfPCell(new Phrase(content, font));
            } else {
                cell = new PdfPCell(new Phrase(new Chunk(content, font).setLocalGoto(appendixNumber.toString())));
            }
        } else {
            cell = new PdfPCell(new Phrase(notProvided, ModelBuilderConfiguration.MEDIUM_GREY_FONT));
        }
        cell.setPaddingBottom(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        return cell;
    }
 
}
