package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_DECLINED_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_SUITABLE_FOR_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_SUITABLE_FOR_OPPORTUNITY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_REFEREE_REFERENCE_APPENDIX;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_REFEREE_SUBHEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_COMMENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_NO;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_RATING;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_YES;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope("prototype")
public class ApplicationDownloadReferenceBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationDownloadReferenceBuilder.class);

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Autowired
    private ApplicationContext applicationContext;

    public byte[] build(final Application application, final Comment referenceComment) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument,
                    applicationContext.getBean(PropertyLoader.class).withResource(application).load(APPLICATION_REFEREE_REFERENCE_APPENDIX));

            addReferenceComment(pdfDocument, body, pdfWriter, application, referenceComment);

            pdfDocument.newPage();
            pdfDocument.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

    public void addReferenceComment(Document pdfDocument, PdfPTable body, PdfWriter pdfWriter, Application application, Comment referenceComment)
            throws DocumentException {
        PropertyLoader propertyLoader = applicationContext.getBean(PropertyLoader.class).withResource(application);
        String comment = propertyLoader.load(SYSTEM_COMMENT);

        if (referenceComment == null) {
            applicationDownloadBuilderHelper.addContentRowMedium(comment,
                    application.isApproved() ? propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT) : null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else if (referenceComment.isDeclinedResponse()) {
            applicationDownloadBuilderHelper.addContentRowMedium(comment, propertyLoader.load(APPLICATION_COMMENT_DECLINED_REFEREE), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_SUBHEADER), referenceComment.getUserDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(comment, referenceComment.getContent(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_RATING), referenceComment.getApplicationRatingDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_COMMENT_SUITABLE_FOR_INSTITUTION),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, referenceComment.getSuitableForInstitution()), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_COMMENT_SUITABLE_FOR_OPPORTUNITY),
                    propertyLoader.load(SYSTEM_YES, SYSTEM_NO, referenceComment.getSuitableForInstitution()), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);

            PdfContentByte content = pdfWriter.getDirectContent();
            for (com.zuehlke.pgadmissions.domain.Document input : referenceComment.getDocuments()) {
                try {
                    PdfReader reader = new PdfReader(input.getContent());
                    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                        pdfDocument.newPage();
                        PdfImportedPage page = pdfWriter.getImportedPage(reader, i);
                        content.addTemplate(page, 0, 0);
                    }
                } catch (IOException e) {
                    LOGGER.error("Unable to append reference for application " + application.getCode() + " referee " + referenceComment.getUserDisplay(), e);
                }
            }
        }
    }
}
