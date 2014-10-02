package com.zuehlke.pgadmissions.services.builders.download;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;

@Component
@Scope("prototype")
public class ApplicationDownloadEquivalentExperienceBuilder {

    @Value("${xml.export.system.qualification}")
    private String equivalentQualification;

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    public byte[] build(final Application application, final Comment approvalComment) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PdfPTable body = applicationDownloadBuilderHelper.newSectionHeader("Equivalent Experience");
            
            PdfPCell cell = applicationDownloadBuilderHelper.newContentCellMedium(approvalComment == null ? null : equivalentQualification);
            cell.setColspan(2);
            body.addCell(cell);
            
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
            pdfDocument.close();
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

}
