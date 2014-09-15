package com.zuehlke.pgadmissions.pdf;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class ApplicationAlternativeQualificationBuilder extends AbstractPdfModelBuilder {
    
    @Value("${xml.export.system.qualification}")
    String noQualificationExplanation;
    
    @Autowired
    private CommentService commentService;

    public byte[] build(final Application application) {
        try {
            Document exportDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(exportDocument, outputStream);
            exportDocument.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(MAX_WIDTH_PERCENTAGE);
            table.addCell(newGrayTableCell("No Transcripts Provided", BOLD_FONT));
            exportDocument.add(table);
            exportDocument.add(addSectionSeparators());

            Comment approvalComment = commentService.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
            
            if (approvalComment == null) {
                exportDocument.add(new Paragraph(String.format("Approval Round Comment:\n%s", NOT_PROVIDED)));
            } else {
                exportDocument.add(new Paragraph(String.format("Approval Round Comment:\n%s", noQualificationExplanation)));
            }
            
            exportDocument.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }
    
}
