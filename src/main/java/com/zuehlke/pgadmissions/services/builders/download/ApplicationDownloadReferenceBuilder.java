package com.zuehlke.pgadmissions.services.builders.download;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DECLINED_REFEREE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REFEREE_REFERENCE_APPENDIX;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_REFEREE_SUBHEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_HEADER;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_RATING;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.dozer.Mapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentCustomResponse;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentCustomResponseRepresentation;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(SCOPE_PROTOTYPE)
public class ApplicationDownloadReferenceBuilder {

    private PropertyLoader propertyLoader;

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;
    
    @Inject
    private DocumentService documentService;
    
    @Inject
    private Mapper mapper;

    public byte[] build(final Application application, final Comment referenceComment) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX));

            addReferenceComment(pdfDocument, body, pdfWriter, application, referenceComment);
            addReferenceDocument(pdfDocument, pdfWriter, referenceComment);

            pdfDocument.newPage();
            pdfDocument.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

    public void addReferenceComment(Document pdfDocument, PdfPTable body, PdfWriter pdfWriter, Application application, Comment referenceComment)
            throws Exception {
        String rowTitle = propertyLoader.load(SYSTEM_COMMENT_HEADER);

        if (referenceComment == null) {
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle,
                    application.isApproved() ? propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT) : null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else if (referenceComment.getDeclinedResponse()) {
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle, propertyLoader.load(APPLICATION_COMMENT_DECLINED_REFEREE), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_SUBHEADER), referenceComment.getUserDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle, referenceComment.getContent(), body);
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_RATING), referenceComment.getApplicationRatingDisplay(), body);
            
            for (CommentCustomResponse customResponse : referenceComment.getCustomResponses()) {
                CommentCustomResponseRepresentation customResponseRepresentation = mapper.map(customResponse, CommentCustomResponseRepresentation.class);
                applicationDownloadBuilderHelper.addContentRowMedium(customResponseRepresentation.getLabel(), customResponseRepresentation.getPropertyValue(), body);
            }
            
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addReferenceDocument(Document pdfDocument, PdfWriter pdfWriter, Comment referenceComment) throws IntegrationException {
        if (referenceComment != null) {
            PdfContentByte content = pdfWriter.getDirectContent();
            for (com.zuehlke.pgadmissions.domain.document.Document document : referenceComment.getDocuments()) {
                try {
                    PdfReader reader = new PdfReader(documentService.getDocumentContent(document));
                    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                        pdfDocument.newPage();
                        PdfImportedPage page = pdfWriter.getImportedPage(reader, i);
                        content.addTemplate(page, 0, 0);
                    }
                } catch (IOException e) {
                    throw new PdfDocumentBuilderException(e);
                }
            }
        }
    }

    public ApplicationDownloadReferenceBuilder localize(PropertyLoader propertyLoader, ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper) {
        this.propertyLoader = propertyLoader;
        this.applicationDownloadBuilderHelper = applicationDownloadBuilderHelper;
        return this;
    }
    
}
