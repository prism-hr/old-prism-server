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
import java.math.BigDecimal;

import javax.inject.Inject;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.comment.CommentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationExport;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

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

    public byte[] build(ApplicationRepresentationExport application, CommentRepresentation commentRepresentation) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, propertyLoader.load(APPLICATION_REFEREE_REFERENCE_APPENDIX));

            addReferenceComment(pdfDocument, body, pdfWriter, application, commentRepresentation);
            addReferenceDocument(pdfDocument, pdfWriter, commentRepresentation);

            pdfDocument.newPage();
            pdfDocument.close();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new PdfDocumentBuilderException(e);
        }
    }

    public void addReferenceComment(Document pdfDocument, PdfPTable body, PdfWriter pdfWriter, ApplicationRepresentationExport application,
            CommentRepresentation referenceComment) throws Exception {
        String rowTitle = propertyLoader.load(SYSTEM_COMMENT_HEADER);

        if (referenceComment == null) {
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle,
                    applicationService.isApproved(application.getId()) ? propertyLoader.load(APPLICATION_REFEREE_REFERENCE_COMMENT_EQUIVALENT) : null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else if (referenceComment.getDeclinedResponse()) {
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle, propertyLoader.load(APPLICATION_COMMENT_DECLINED_REFEREE), body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(APPLICATION_REFEREE_SUBHEADER), referenceComment.getUser().getFullName(),
                    body);
            applicationDownloadBuilderHelper.addContentRowMedium(rowTitle, referenceComment.getContent(), body);

            BigDecimal rating = referenceComment.getRating();
            applicationDownloadBuilderHelper.addContentRowMedium(propertyLoader.load(SYSTEM_RATING), rating == null ? null : rating.toPlainString(), body);

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
