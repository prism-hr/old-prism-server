package uk.co.alumeni.prism.services;

import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.application.Application;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.exceptions.PdfDocumentBuilderException;
import uk.co.alumeni.prism.mapping.ApplicationMapper;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationBatchedDownloadRepresentation;
import uk.co.alumeni.prism.services.builders.download.ApplicationDownloadBuilder;
import uk.co.alumeni.prism.services.builders.download.ApplicationDownloadBuilderHelper;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;

@Component
public class ApplicationDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDownloadService.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Value("${integration.amazon.upload.completion.queue}")
    private String uploadCompletionQueue;

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

    private Path tempDirectory;

    private Map<String, ApplicationBatchedDownloadRepresentation> batchDownloadStatuses = new HashMap<>();

    @PostConstruct
    public void init() throws Exception {
        tempDirectory = Files.createTempDirectory("batch-pdf-");
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        cleanUp(true);
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanUpScheduled() throws IOException {
        cleanUp(false);
    }

    public String build(List<Integer> applicationIds) throws IOException {

        Files.walk(tempDirectory, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(System.out::println);

        User user = userService.getCurrentUser();

        Path tempFile = Files.createTempFile(tempDirectory, null, ".pdf");
        OutputStream outputStream = new FileOutputStream(tempFile.toFile());
        String fileId = tempFile.getFileName().toString().replace(".pdf", "");
        ApplicationBatchedDownloadRepresentation status = new ApplicationBatchedDownloadRepresentation(fileId, 0);
        batchDownloadStatuses.put(fileId, status);
        Runnable reader = () -> {
            PropertyLoader generalPropertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
            ApplicationDownloadBuilderHelper generalApplicationDownloadBuilderHelper =
                    applicationContext.getBean(ApplicationDownloadBuilderHelper.class).localize(generalPropertyLoader);

            try {
                Document pdfDocument = generalApplicationDownloadBuilderHelper.startDocument();
                PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
                pdfDocument.open();

                HashMap<Program, PropertyLoader> specificPropertyLoaders = Maps.newHashMap();
                List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(user, APPLICATION, newArrayList(applicationIds));
                HashMap<Program, ApplicationDownloadBuilderHelper> specificApplicationDownloadBuilderHelpers = Maps.newHashMap();


                User currentUser = userService.getCurrentUser();
                for (int i = 0; i < applicationIds.size(); i++) {
                    Integer applicationId = applicationIds.get(i);
                    applicationContext.getBean(ApplicationDownloadService.class)
                            .processApplicationToPdfDocument(pdfDocument, pdfWriter, specificPropertyLoaders, overridingRoles, specificApplicationDownloadBuilderHelpers,
                                    currentUser, applicationId);
                    status.setPercentReady((i + 1) * 100 / applicationIds.size());
                }

                pdfDocument.close();
            } catch (Exception e) {
                logger.error("Error downloading applications for " + user.getFullName(), e);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        };

        executorService.submit(reader);
        return fileId;
    }

    public void build(List<Integer> applicationIds, OutputStream outputStream) {
        User user = userService.getCurrentUser();

        PropertyLoader generalPropertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
        ApplicationDownloadBuilderHelper generalApplicationDownloadBuilderHelper = applicationContext.getBean(ApplicationDownloadBuilderHelper.class)
                .localize(generalPropertyLoader);

        try {
            Document pdfDocument = generalApplicationDownloadBuilderHelper.startDocument();
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
            pdfDocument.open();

            HashMap<Program, PropertyLoader> specificPropertyLoaders = Maps.newHashMap();
            List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(userService.getCurrentUser(), APPLICATION, newArrayList(applicationIds));
            HashMap<Program, ApplicationDownloadBuilderHelper> specificApplicationDownloadBuilderHelpers = Maps.newHashMap();

            User currentUser = userService.getCurrentUser();
            for (Integer applicationId : applicationIds) {
                applicationContext.getBean(ApplicationDownloadService.class).processApplicationToPdfDocument(pdfDocument,
                        pdfWriter, specificPropertyLoaders, overridingRoles, specificApplicationDownloadBuilderHelpers, currentUser, applicationId);
            }

            pdfDocument.close();
        } catch (Exception e) {
            logger.error("Error downloading applications for " + user.getFullName(), e);
        }
    }

    @Transactional
    public void processApplicationToPdfDocument(Document pdfDocument, PdfWriter pdfWriter, HashMap<Program, PropertyLoader> specificPropertyLoaders,
                                                List<PrismRole> overridingRoles, HashMap<Program, ApplicationDownloadBuilderHelper> specificApplicationDownloadBuilderHelpers, User currentUser,
                                                Integer applicationId) {
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
                    .build(applicationMapper.getApplicationRepresentationExtended(application, overridingRoles, currentUser), pdfDocument, pdfWriter);
        } catch (PdfDocumentBuilderException e) {
            logger.error("Error building download for application " + application.getCode(), e);
        }

        pdfDocument.newPage();
    }

    public ApplicationBatchedDownloadRepresentation getStatus(String fileId) throws IOException {
        return batchDownloadStatuses.get(fileId);
    }

    public void getPdfBatch(String fileId, HttpServletResponse response) throws IOException {
        Files.copy(tempDirectory.resolve(fileId + ".pdf"), response.getOutputStream());
    }

    private void cleanUp(boolean all) throws IOException {
        Files.walk(tempDirectory, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(file -> all || file.lastModified() < DateTime.now().minusHours(1).getMillis())
                .peek(System.out::println)
                .forEach(File::delete);

        if (all) {
            tempDirectory.toFile().delete();
        }
    }

}
