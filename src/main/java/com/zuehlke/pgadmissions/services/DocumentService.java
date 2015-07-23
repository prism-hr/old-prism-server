package com.zuehlke.pgadmissions.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.itextpdf.text.pdf.PdfReader;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory;
import com.zuehlke.pgadmissions.domain.document.PrismFileCategory.PrismImageCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityType;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PrismBadRequestException;
import com.zuehlke.pgadmissions.services.helpers.processors.ImageDocumentProcessor;
import com.zuehlke.pgadmissions.services.scrapping.ImportedDataScraper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.io.Streams;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.http.Part;
import java.io.*;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.document.PrismFileCategory.DOCUMENT;
import static com.zuehlke.pgadmissions.domain.document.PrismFileCategory.IMAGE;

@Service
@Transactional
public class DocumentService {

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${integration.amazon.bucket}")
    private String amazonBucket;

    @Value("${integration.amazon.production.bucket}")
    private String amazonProductionBucket;

    @Inject
    private DocumentDAO documentDAO;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public Document getById(Integer id) {
        return entityService.getById(Document.class, id);
    }

    public Document getById(Integer id, PrismFileCategory category) {
        return entityService.getByProperties(Document.class, ImmutableMap.<String, Object> of("id", id, "category", category));
    }

    public Document createDocument(Part uploadStream) throws Exception {
        return create(DOCUMENT, getFileName(uploadStream), Streams.readAll(uploadStream.getInputStream()), uploadStream.getContentType(), null, null);
    }

    public Document createImage(Part uploadStream, Integer institutionId, PrismImageCategory imageCategory) throws Exception {
        return create(IMAGE, getFileName(uploadStream), Streams.readAll(uploadStream.getInputStream()), uploadStream.getContentType(), institutionId,
                imageCategory);
    }

    public Document create(PrismFileCategory category, String fileName, byte[] content, String contentType, Integer entityId, PrismImageCategory imageCategory) {
        Preconditions.checkNotNull(category);

        boolean image = category == IMAGE;
        User user = userService.getCurrentUser();
        if (image) {
            Class<? extends ImageDocumentProcessor> processor = imageCategory.getImageProcessor();
            if (processor != null) {
                content = applicationContext.getBean(processor).process(content, contentType);
            }
            contentType = "image/jpeg";
        } else if (category == DOCUMENT) {
            Preconditions.checkNotNull(user);
            validatePdfDocument(content);
        }
        Document document = new Document().withContent(content).withContentType(contentType).withExported(false).withFileName(fileName)
                .withUser(user).withCreatedTimestamp(new DateTime()).withCategory(category);
        entityService.save(document);

        if (image && entityId != null) {
            applicationContext.getBean(imageCategory.getImagePersister()).persist(entityId, document);
        }

        return document;
    }

    public void deleteOrphanDocuments(DateTime baselineTime) throws IOException {
        List<Integer> documentIds = documentDAO.getOrphanDocuments(baselineTime);
        if (!documentIds.isEmpty()) {
            documentDAO.deleteOrphanDocuments(documentIds);
        }
    }

    public void validateDownload(Document document) {
        User user = userService.getCurrentUser();
        Resource resource = document.getResource();
        if (!user.getId().equals(document.getUser().getId())) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            actionService.validateViewEditAction(resource, viewEditAction, user);
        }
    }

    public byte[] getDocumentContent(Integer document) throws IntegrationException {
        return getDocumentContent(getById(document));
    }

    public byte[] getDocumentContent(Document document) throws IntegrationException {
        if (document.getExported()) {
            AmazonS3 amazonClient = getAmazonClient();
            String amazonObjectKey = document.getExportFilenameAmazon();

            try {
                return getAmazonObject(amazonClient, amazonObjectKey);
            } catch (AmazonS3Exception e1) {
                if (!contextEnvironment.equals("prod")) {
                    try {
                        amazonClient.copyObject(amazonProductionBucket, amazonObjectKey, amazonBucket, amazonObjectKey);
                        return getAmazonObject(amazonClient, amazonObjectKey);
                    } catch (Exception e2) {
                        return getFallbackDocument(document);
                    }
                }
                return getFallbackDocument(document);
            }
        }

        return document.getContent();
    }

    public List<Integer> getExportDocuments() {
        return documentDAO.getExportDocuments();
    }

    public void exportDocumentToAmazon(Integer documentId) throws IOException, IntegrationException {
        Document document = getById(documentId);
        if (!document.getExported()) {
            AmazonS3 amazonClient = getAmazonClient();

            ObjectMetadata amazonMetadata = new ObjectMetadata();
            amazonMetadata.setContentType(document.getContentType());
            byte[] content = document.getContent();
            amazonMetadata.setContentLength(content.length);

            ByteArrayInputStream amazonStream = null;

            try {
                amazonStream = new ByteArrayInputStream(content);
                PutObjectRequest amazonRequest = new PutObjectRequest(amazonBucket, document.getExportFilenameAmazon(), amazonStream, amazonMetadata);
                amazonClient.putObject(amazonRequest);
                document.setContent(null);
                document.setExported(true);
            } finally {
                IOUtils.closeQuietly(amazonStream);
            }
        }
    }

    public void deleteAmazonDocuments(DateTime baselineTime) throws IOException, IntegrationException {
        LocalDate baselineDate = baselineTime.toLocalDate();
        System system = systemService.getSystem();
        LocalDate lastAmazonCleanupDate = system.getLastAmazonCleanupDate();
        if (lastAmazonCleanupDate == null || lastAmazonCleanupDate.isBefore(baselineDate)) {
            AmazonS3 amazonClient = getAmazonClient();
            ListObjectsRequest amazonRequest = new ListObjectsRequest().withBucketName(amazonBucket);
            ObjectListing amazonObjects = amazonClient.listObjects(amazonRequest);

            for (S3ObjectSummary amazonObject : amazonObjects.getObjectSummaries()) {
                String amazonObjectKey = amazonObject.getKey();
                Document document = getById(Integer.parseInt(amazonObjectKey));

                if (document == null) {
                    amazonClient.deleteObject(amazonBucket, amazonObjectKey);
                }
            }

            system.setLastAmazonCleanupDate(baselineDate);
        }
    }

    public byte[] getSystemDocument(String path) {
        try {
            return Resources.toByteArray(Resources.getResource(path));
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public void reassignDocuments(User oldUser, User newUser) {
        documentDAO.reassignDocuments(oldUser, newUser);
    }

    public InputStream getImportedDataSource(ImportedEntityType importedEntityType) throws Exception {
        AmazonS3 amazonClient = getAmazonClient();
        String bucketName = "prism-import-data";
        String fileName = StringUtils.uncapitalize(importedEntityType.getId().getUpperCamelName().replace("Imported", "")) + ".json";

        DateTime lastModified;
        try {
            ObjectMetadata importDataObjectMetadata = amazonClient.getObjectMetadata(bucketName, fileName);
            lastModified = new DateTime(importDataObjectMetadata.getLastModified());
        } catch (AmazonServiceException e) {
            lastModified = null;
        }
        Class<? extends ImportedDataScraper> scraperClass = importedEntityType.getId().getScraperClass();
        if (scraperClass != null && (lastModified == null || lastModified.isBefore(DateTime.now().minusYears(1)))) {
            ImportedDataScraper scraper = applicationContext.getBean(scraperClass);
            File file = File.createTempFile(fileName, null);
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8)) {
                scraper.scrape(writer);
            }
            amazonClient.putObject(bucketName, fileName, file);
        }

        S3Object importDataObject = amazonClient.getObject(bucketName, fileName);
        lastModified = new DateTime(importDataObject.getObjectMetadata().getLastModified());
        if (importedEntityType.getLastImportedTimestamp() == null || lastModified.isAfter(importedEntityType.getLastImportedTimestamp())) {
            return importDataObject.getObjectContent();
        }
        return null;
    }

    private void validatePdfDocument(byte[] content) {
        PdfReader pdfReader;
        try {
            pdfReader = new PdfReader(content);
        } catch (IOException e) {
            throw new PrismBadRequestException("Uploaded file is not valid PDF file");
        }

        if (!pdfReader.isOpenedWithFullPermissions()) {
            throw new PrismBadRequestException("You cannot upload an encrypted file");
        }
    }

    private AmazonS3 getAmazonClient() throws IntegrationException {
        return new AmazonS3Client(systemService.getAmazonCredentials());
    }

    private byte[] getAmazonObject(AmazonS3 amazonClient, String amazonObjectKey) throws IntegrationException {
        GetObjectRequest amazonRequest = new GetObjectRequest(amazonBucket, amazonObjectKey);
        S3Object amazonObject = amazonClient.getObject(amazonRequest);

        S3ObjectInputStream amazonStream = null;
        try {
            amazonStream = amazonObject.getObjectContent();
            byte[] amazonByteArray = IOUtils.toByteArray(amazonStream);
            IOUtils.closeQuietly(amazonStream);
            return amazonByteArray;
        } catch (IOException e) {
            throw new IntegrationException(e);
        } finally {
            IOUtils.closeQuietly(amazonStream);
        }
    }

    private String getFileName(Part upload) {
        for (String cd : upload.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private byte[] getFallbackDocument(Document document) {
        return getSystemDocument("document/" + (document.getCategory() == PrismFileCategory.DOCUMENT ? "document_exported.pdf" : "image_exported.jpg"));
    }

}
