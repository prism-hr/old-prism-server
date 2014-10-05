package com.zuehlke.pgadmissions.services.builders.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.zuehlke.pgadmissions.utils.ConversionUtils;

@Component
@Scope("prototype")
public class ApplicationDownloadReferenceBuilder {

    private static Logger LOGGER = LoggerFactory.getLogger(ApplicationDownloadReferenceBuilder.class);

    @Value("${xml.export.system.reference}")
    private String equivalentReference;

    @Autowired
    private ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper;

    public byte[] build(final Application application, final Comment referenceComment) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);

            PdfPTable body = applicationDownloadBuilderHelper.startSection(pdfDocument, "Reference");

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
        if (referenceComment == null) {
            applicationDownloadBuilderHelper.addContentRowMedium("Comment", application.isApproved() ? equivalentReference : null, body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else if (referenceComment.isDeclinedResponse()) {
            applicationDownloadBuilderHelper.addContentRowMedium("Comment", "Declined to provide a reference.", body);
            applicationDownloadBuilderHelper.closeSection(pdfDocument, body);
        } else {
            applicationDownloadBuilderHelper.addContentRowMedium("Referee", referenceComment.getUserDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium("Comment", referenceComment.getContent(), body);
            applicationDownloadBuilderHelper.addContentRowMedium("Rating", referenceComment.getApplicationRatingDisplay(), body);
            applicationDownloadBuilderHelper.addContentRowMedium("Suitable for Recruiting Institution?",
                    ConversionUtils.booleanToString(referenceComment.getSuitableForInstitution(), "Yes", "No"), body);
            applicationDownloadBuilderHelper.addContentRowMedium("Suitable for Recruiting Position?",
                    ConversionUtils.booleanToString(referenceComment.getSuitableForOpportunity(), "Yes", "No"), body);
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
