package uk.co.alumeni.prism.services;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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
    private DocumentService documentService;

    @Inject
    private ApplicationMapper applicationMapper;
    
    @Inject
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;
    
    public String build(List<Integer> applicationIds, OutputStream outputStream) {
        boolean batchDownloading = false;
        User user = userService.getCurrentUser();
        
        OutputStream finalOutputStream;
        String uuid = UUID.randomUUID().toString();
        if (outputStream == null) {
            finalOutputStream = new PipedOutputStream();
            batchDownloading = true;
        } else {
            finalOutputStream = outputStream;
        }
        
        boolean finalBatchDownloading = batchDownloading;
        Runnable download = () -> {
            PropertyLoader generalPropertyLoader = applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem());
            ApplicationDownloadBuilderHelper generalApplicationDownloadBuilderHelper = applicationContext.getBean(ApplicationDownloadBuilderHelper.class)
                    .localize(generalPropertyLoader);

            try {
                Document pdfDocument = generalApplicationDownloadBuilderHelper.startDocument();
                PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, finalOutputStream);
                pdfDocument.open();
    
                HashMap<Program, PropertyLoader> specificPropertyLoaders = Maps.newHashMap();
                List<PrismRole> overridingRoles = roleService.getRolesOverridingRedactions(userService.getCurrentUser(), APPLICATION, newArrayList(applicationIds));
                HashMap<Program, ApplicationDownloadBuilderHelper> specificApplicationDownloadBuilderHelpers = Maps.newHashMap();
    
                User currentUser = userService.getCurrentUser();
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
                                .build(applicationMapper.getApplicationRepresentationExtended(application, overridingRoles, currentUser), pdfDocument, pdfWriter);
                    } catch (PdfDocumentBuilderException e) {
                        logger.error("Error building download for application " + application.getCode(), e);
                    }
                    
                    pdfDocument.newPage();
                }
    
                pdfDocument.close();
                if (finalBatchDownloading) {
                    documentService.exportBatchedDocumentToAmazon(uuid, (PipedOutputStream) outputStream);
                }
            } catch (Exception e) {
                logger.error("Error downloading applications for " + user.getFullName(), e);
            } finally {
                if (finalBatchDownloading) {
                    IOUtils.closeQuietly(finalOutputStream);
                }
            }
        };
        
        if (batchDownloading) {
            executorService.submit(download);
        } else {
            download.run();
        }
    
        return uuid;
    }
    
    public ApplicationBatchedDownloadRepresentation getStatus(String uuid) throws IOException {
        AmazonSQSClient client = new AmazonSQSClient(systemService.getAmazonCredentials());
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(uploadCompletionQueue);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        
        while (true) {
            ReceiveMessageResult result = client.receiveMessage(receiveMessageRequest);
            List<Message> messages = result.getMessages();
            if (messages.isEmpty()) {
                // Depend upon next client request to find the message
                return new ApplicationBatchedDownloadRepresentation().setUuid(uuid);
            }
            
            for (Message message : messages) {
                UploadEventMessage uploadEventMessage = objectMapper.readValue(message.getBody(), UploadEventMessage.class);
                for (UploadEventMessage.Record record : uploadEventMessage.getRecords()) {
                    if (record.getS3().getObject().getKey().endsWith(uuid)) {
                        return new ApplicationBatchedDownloadRepresentation().setUuid(uuid).setReady(true);
                    }
                }
            }
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class UploadEventMessage {
        
        List<Record> records = new ArrayList<>();
    
        public List<Record> getRecords() {
            return records;
        }
    
        public void setRecords(List<Record> records) {
            this.records = records;
        }
    
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Record {
            
            private S3 s3;
    
            public S3 getS3() {
                return s3;
            }
    
            public void setS3(S3 s3) {
                this.s3 = s3;
            }
    
            @JsonIgnoreProperties(ignoreUnknown = true)
            private static class S3 {
                
                private S3Object object;
    
                public S3Object getObject() {
                    return object;
                }
    
                public void setObject(S3Object object) {
                    this.object = object;
                }
    
                @JsonIgnoreProperties(ignoreUnknown = true)
                private static class S3Object {
                    
                    private String key;
    
                    public String getKey() {
                        return key;
                    }
    
                    public void setKey(String key) {
                        this.key = key;
                    }
                    
                }
                
            }
            
        }
        
    }
    
}
