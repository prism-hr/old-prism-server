package com.zuehlke.pgadmissions.services.builders.pdf;

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
public class AlternativeQualificationBuilder {

    @Value("${xml.export.not.provided}")
    String notProvided;
    
    @Value("${xml.export.system.qualification}")
    String qualification;
    
    @Autowired
    private ModelBuilderHelper modelBuilderHelper;
    
    @Autowired
    private CommentService commentService;

    public byte[] build(final Application application) {
        try {
            Document exportDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(exportDocument, outputStream);
            exportDocument.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(ModelBuilderConfiguration.PAGE_WIDTH);
            table.addCell(modelBuilderHelper.newColoredTableCell("No Transcripts Provided", ModelBuilderConfiguration.BOLD_FONT));
            exportDocument.add(table);
            exportDocument.add(modelBuilderHelper.newSectionSeparator());

            Comment approvalComment = commentService.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
            
            if (approvalComment == null) {
                exportDocument.add(new Paragraph(String.format("Comment:\n", notProvided)));
            } else {
                exportDocument.add(new Paragraph(String.format("Comment:\n", qualification)));
            }
            
            exportDocument.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }
    
}
