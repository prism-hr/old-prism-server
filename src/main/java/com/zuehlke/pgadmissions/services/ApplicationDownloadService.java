package com.zuehlke.pgadmissions.services;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionEnhancement;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.ApplicationDownloadDTO;
import com.zuehlke.pgadmissions.exceptions.PdfDocumentBuilderException;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilder;
import com.zuehlke.pgadmissions.services.builders.download.ApplicationDownloadBuilderHelper;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Component
@Scope(SCOPE_PROTOTYPE)
public class ApplicationDownloadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDownloadService.class);

    @Autowired
    private ActionService actionService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    public void build(ApplicationDownloadDTO applicationDownloadDTO, PropertyLoader propertyLoader,
            ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper, final OutputStream outputStream) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);
            applicationContext.getBean(ApplicationDownloadBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                    .build(applicationDownloadDTO, pdfDocument, pdfWriter);
            pdfDocument.newPage();
            pdfDocument.close();
        } catch (Exception e) {
            LOGGER.error("Error building download for application " + applicationDownloadDTO.getApplication().getCode(), e);
        }
    }

    public byte[] build(Integer... applicationIds) {
        User user = userService.getCurrentUser();

        PropertyLoader generalPropertyLoader = new PropertyLoader().localize(systemService.getSystem(), user);
        ApplicationDownloadBuilderHelper generalApplicationDownloadBuilderHelper = new ApplicationDownloadBuilderHelper().localize(generalPropertyLoader);

        try {
            Document pdfDocument = generalApplicationDownloadBuilderHelper.startDocument();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, baos);
            pdfDocument.open();

            HashMap<Program, PropertyLoader> specificPropertyLoaders = Maps.newHashMap();
            HashMap<Program, ApplicationDownloadBuilderHelper> specificApplicationDownloadBuilderHelpers = Maps.newHashMap();

            for (Integer applicationId : applicationIds) {
                Application application = applicationService.getById(applicationId);
                Program program = application.getProgram();

                PropertyLoader specificPropertyLoader = specificPropertyLoaders.get(program);
                ApplicationDownloadBuilderHelper specificApplicationDownloadBuilderHelper = specificApplicationDownloadBuilderHelpers.get(program);

                specificPropertyLoaders
                        .put(program, specificPropertyLoader == null ? new PropertyLoader().localize(application, user) : specificPropertyLoader);
                specificApplicationDownloadBuilderHelpers
                        .put(program,
                                specificApplicationDownloadBuilderHelper == null ? new ApplicationDownloadBuilderHelper().localize(specificPropertyLoaders
                                        .get(program)) : specificApplicationDownloadBuilderHelper);

                try {
                    List<PrismActionEnhancement> actionEnhancements = actionService.getPermittedActionEnhancements(application, user);
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

                        applicationContext.getBean(ApplicationDownloadBuilder.class)
                                .localize(specificPropertyLoaders.get(program), specificApplicationDownloadBuilderHelpers.get(program))
                                .build(applicationDownloadDTO, pdfDocument, pdfWriter);
                    }
                } catch (PdfDocumentBuilderException e) {
                    LOGGER.warn("Error building download for application " + application.getCode(), e);
                }
                pdfDocument.newPage();
            }

            pdfDocument.close();

            return baos.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Error downloading applications for " + user.getFullName(), e);
        }

        return null;
    }

}
