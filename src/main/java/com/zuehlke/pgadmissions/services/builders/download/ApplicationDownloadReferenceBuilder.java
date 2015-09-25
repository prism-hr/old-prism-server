package com.zuehlke.pgadmissions.services.builders.download;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.*;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExtended;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.*;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

// TODO move this shit into the adapter
@Component
@Scope(SCOPE_PROTOTYPE)
public class ApplicationDownloadReferenceBuilder {

    private PropertyLoader propertyLoader;

    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private DocumentService documentService;

    public byte[] build(ApplicationRepresentationExtended application, CommentRepresentation commentRepresentation) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.loadLazy(PROFILE_REFEREE_REFERENCE_APPENDIX));

            addReferenceComment(pdfDocument, body, pdfWriter, application, commentRepresentation);
            addReferenceDocument(pdfDocument, pdfWriter, commentRepresentation);

            pdfDocument.newPage();
            pdfDocument.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

    public void addReferenceComment(Document pdfDocument, PdfPTable body, PdfWriter pdfWriter, ApplicationRepresentationExtended application,
            CommentRepresentation referenceComment) throws Exception {
        String rowTitle = propertyLoader.loadLazy(SYSTEM_COMMENT_HEADER);

        if (referenceComment == null) {
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle,
                    applicationService.isApproved(application.getId()) ? propertyLoader.loadLazy(PROFILE_REFEREE_REFERENCE_COMMENT_EQUIVALENT) : null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else if (referenceComment.getDeclinedResponse()) {
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle, propertyLoader.loadLazy(APPLICATION_COMMENT_DECLINED_REFEREE), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(PROFILE_REFEREE_SUBHEADER), referenceComment.getUser().getFullName(),
                    body);
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle, referenceComment.getContent(), body);

            BigDecimal rating = referenceComment.getRating();
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.loadLazy(SYSTEM_RATING), rating == null ? null : rating.toPlainString(), body);

            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        }
    }

    private void addReferenceDocument(Document pdfDocument, PdfWriter pdfWriter, CommentRepresentation referenceComment) throws IntegrationException {
        if (referenceComment != null) {
            PdfContentByte content = pdfWriter.getDirectContent();
            for (DocumentRepresentation document : referenceComment.getDocuments()) {
                try {
                    PdfReader reader = new PdfReader(documentService.getDocumentContent(document.getId()));
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
