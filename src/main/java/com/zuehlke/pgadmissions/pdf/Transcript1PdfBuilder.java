package com.zuehlke.pgadmissions.pdf;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.CommentService;

@Component
public class Transcript1PdfBuilder extends AbstractPdfModelBuilder {
    
    @Autowired
    private CommentService commentService;

    public Transcript1PdfBuilder() {
    }

    public byte[] build(final Application application) {
        try {
            Document document = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newGrayTableCell("No Transcripts Provided", BOLD_FONT));
            document.add(table);
            document.add(addSectionSeparators());

            Comment latestApprovalComment = commentService.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
            if (latestApprovalComment != null) {
                if (StringUtils.isBlank(latestApprovalComment.getEquivalentExperience())) {
                    document.add(new Paragraph(String.format("Approval Round Comment:\n%s", latestApprovalComment.getEquivalentExperience())));
                } else {
                    document.add(new Paragraph(String.format("Approval Round Comment:\n%s", NOT_PROVIDED)));
                }
            } else {
                document.add(new Paragraph(String.format("Approval Round Comment:\n%s", NOT_PROVIDED)));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }
}
