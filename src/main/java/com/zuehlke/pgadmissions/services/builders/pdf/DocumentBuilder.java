package com.zuehlke.pgadmissions.services.builders.pdf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class DocumentBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentBuilder.class);

    @Autowired
    private ModelBuilder modelBuilder;

    @Autowired
    private ActionService actionService;

    @Autowired
    private UserService userService;

    public void build(ApplicationDownloadDTO applicationDownloadDTO, final OutputStream outputStream) {
        try {
            Document pdfDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
            pdfWriter.setCloseStream(false);
            pdfDocument.open();
            modelBuilder.build(applicationDownloadDTO, pdfDocument, pdfWriter);
            pdfDocument.newPage();
            pdfDocument.close();
        } catch (Exception e) {
            LOGGER.error("Error appending download for application " + applicationDownloadDTO.getApplication().getCode(), e);
        }
    }

    public byte[] build(final Application... applications) {
        User currentUser = userService.getCurrentUser();
        
        try {
            Document pdfDocument = new Document(PageSize.A4, 50, 50, 100, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, baos);
            pdfDocument.open();
            
            for (Application application : applications) {
                try {
                    List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(application, currentUser); 
                    if (actionEnhancements.size() > 0) {
                        boolean includeEqualOpportunitiesData = actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_AS_CREATOR)
                                || actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER)
                                || actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_CREATOR)
                                || actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER);
    
                        boolean includeReferences = actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_AS_RECRUITER)
                                || actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_AS_ADMITTER)
                                || actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_RECRUITER)
                                || actionEnhancements.contains(PrismActionEnhancement.APPLICATION_VIEW_EDIT_AS_ADMITTER);
    
                        ApplicationDownloadDTO applicationDownloadDTO = new ApplicationDownloadDTO().withApplication(application)
                                .withIncludeEqualOpportuntiesData(includeEqualOpportunitiesData).withIncludeReferences(includeReferences);
    
                        modelBuilder.build(applicationDownloadDTO, pdfDocument, pdfWriter);
                    }
                } catch (PdfDocumentBuilderException e) {
                    LOGGER.warn("Error building download for application " + application.getCode(), e);
                }
                pdfDocument.newPage();
            }

            pdfDocument.close();

            return baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Error downloading applications for " + currentUser.toString(), e);
        }
        return null;
    }

}
