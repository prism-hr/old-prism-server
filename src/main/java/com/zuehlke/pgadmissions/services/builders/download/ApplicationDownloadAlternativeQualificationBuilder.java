package com.zuehlke.pgadmissions.services.builders.download;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.CommentService;

@Component
@Scope("prototype")
public class ApplicationDownloadAlternativeQualificationBuilder {

    @Value("${xml.export.not.provided}")
    String notProvided;

    @Value("${xml.export.system.qualification}")
    String alternativeQualification;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    public byte[] build(final Application application) {
        try {
            Document document = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(ApplicationDownloadBuilderConfiguration.PAGE_WIDTH);
            table.addCell(applicationDownloadBuilderHelper.newSectionHeader("No Qualification Transcript"));
            document.add(table);
            document.add(applicationDownloadBuilderHelper.newSectionSeparator());

            Comment approvalComment = commentService.getLatestComment(application, PrismAction.APPLICATION_ASSIGN_SUPERVISORS);
            document.add(applicationDownloadBuilderHelper.newContentCellMedium("Comment: "
                    + (approvalComment == null ? notProvided : alternativeQualification)));

            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

}
