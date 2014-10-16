package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_QUALIFICATION_EQUIVALENT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_QUALIFICATION_EXPERIENCE_MESSAGE;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ApplicationDownloadEquivalentExperienceBuilder {

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Autowired
    private ApplicationContext applicationContext;

    public byte[] build(final Application application, final Comment approvalComment) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class);

            PdfPTable body = applicationDownloadBuilderHelper.newSectionHeader(propertyLoader.load(APPLICATION_QUALIFICATION_EQUIVALENT_HEADER));
            PdfPCell cell = applicationDownloadBuilderHelper.newContentCellMedium(approvalComment == null ? null : propertyLoader
                    .load(APPLICATION_QUALIFICATION_EXPERIENCE_MESSAGE));
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
