package uk.co.alumeni.prism.services;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.exceptions.PdfDocumentBuilderException;
import uk.co.alumeni.prism.mapping.ApplicationMapper;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationExtended;
import uk.co.alumeni.prism.services.builders.download.ApplicationDownloadBuilder;
import uk.co.alumeni.prism.services.builders.download.ApplicationDownloadBuilderHelper;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

@Component
@Scope(SCOPE_PROTOTYPE)
public class ApplicationDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDownloadService.class);

    @Inject
    private ApplicationService applicationService;

    @Inject
    private SystemService systemService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private ApplicationContext applicationContext;

    public void build(ApplicationRepresentationExtended application, PropertyLoader propertyLoader,
            ApplicationDownloadBuilderHelper applicationDownloadBuilderHelper, final OutputStream outputStream) {
        try {
            Document pdfDocument = applicationDownloadBuilderHelper.startDocument();
            PdfWriter pdfWriter = applicationDownloadBuilderHelper.startDocumentWriter(outputStream, pdfDocument);
            applicationContext.getBean(ApplicationDownloadBuilder.class).localize(propertyLoader, applicationDownloadBuilderHelper)
                    .build(application, pdfDocument, pdfWriter);
            pdfDocument.newPage();
            pdfDocument.close();
        } catch (Exception e) {
            logger.error("Error building download for application " + application.getCode(), e);
        }
    }

    public void build(OutputStream oStream, Integer... applicationIds) {
        User user = userService.getCurrentUser();

        PropertyLoader generalPropertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        ApplicationDownloadBuilderHelper generalApplicationDownloadBuilderHelper = applicationContext.getBean(ApplicationDownloadBuilderHelper.class).localize(
                generalPropertyLoader);

        try {
            Document pdfDocument = generalApplicationDownloadBuilderHelper.startDocument();
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, oStream);
            pdfDocument.open();

            HashMap<Program, PropertyLoader> specificPropertyLoaders = Maps.newHashMap();
            List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(userService.getCurrentUser(), APPLICATION, Lists.newArrayList(applicationIds));
            HashMap<Program, ApplicationDownloadBuilderHelper> specificApplicationDownloadBuilderHelpers = Maps.newHashMap();

            for (Integer applicationId : applicationIds) {
                Application application = applicationService.getById(applicationId);
                Program program = application.getProgram();

                PropertyLoader propertyLoader = specificPropertyLoaders.get(program);
                if (propertyLoader == null) {
                    propertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(application);
                }
                specificPropertyLoaders.put(program, propertyLoader);

                ApplicationDownloadBuilderHelper downloadBuilderHelper = specificApplicationDownloadBuilderHelpers.get(program);
                if (downloadBuilderHelper == null) {
                    downloadBuilderHelper = applicationContext.getBean(ApplicationDownloadBuilderHelper.class).localize(specificPropertyLoaders.get(program));
                }
                specificApplicationDownloadBuilderHelpers.put(program, downloadBuilderHelper);

                try {
                    applicationContext.getBean(ApplicationDownloadBuilder.class)
                            .localize(specificPropertyLoaders.get(program), specificApplicationDownloadBuilderHelpers.get(program))
                            .build(applicationMapper.getApplicationRepresentationExtended(application, overridingRoles), pdfDocument, pdfWriter);
                } catch (PdfDocumentBuilderException e) {
                    logger.error("Error building download for application " + application.getCode(), e);
                }
                pdfDocument.newPage();
            }

            pdfDocument.close();

        } catch (Exception e) {
            logger.error("Error downloading applications for " + user.getFullName(), e);
        }
    }

}
