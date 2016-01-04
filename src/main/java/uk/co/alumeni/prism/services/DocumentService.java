package uk.co.alumeni.prism.services;

import static uk.co.alumeni.prism.domain.document.PrismFileCategory.DOCUMENT;
import static uk.co.alumeni.prism.domain.document.PrismFileCategory.IMAGE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.io.Streams;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.itextpdf.text.pdf.PdfReader;

import uk.co.alumeni.prism.dao.DocumentDAO;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.document.PrismFileCategory;
import uk.co.alumeni.prism.domain.document.PrismFileCategory.PrismImageCategory;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.exceptions.IntegrationException;
import uk.co.alumeni.prism.exceptions.PrismBadRequestException;
import uk.co.alumeni.prism.exceptions.WorkflowPermissionException;

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
        return entityService.getByProperties(Document.class, ImmutableMap.<String, Object>of("id", id, "category", category));
    }

    public Document createDocument(Part uploadStream) throws IOException {
        try (InputStream iStream = uploadStream.getInputStream()) {
            return create(DOCUMENT, getFileName(uploadStream), Streams.readAll(iStream), uploadStream.getContentType(), null, null);
        }
    }

    public Document createImage(Part uploadStream, Integer institutionId, PrismImageCategory imageCategory) throws IOException {
        try (InputStream iStream = uploadStream.getInputStream()) {
            return create(IMAGE, getFileName(uploadStream), Streams.readAll(iStream), uploadStream.getContentType(), institutionId, imageCategory);
        }
    }

    public Document create(PrismFileCategory category, String fileName, byte[] content, String contentType, Integer entityId, PrismImageCategory imageCategory) {
        Preconditions.checkNotNull(category);

        boolean image = category == IMAGE;
        User user = userService.getCurrentUser();
        if (image) {
            contentType = "image/jpeg";
        } else if (category == DOCUMENT) {
            Preconditions.checkNotNull(user);
            validatePdfDocument(content);
        }
        Document document = new Document().withContent(content).withContentType(contentType).withExported(false).withFileName(fileName + "")
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

    public List<Document> getResourceOwnerDocuments(Resource resource) {
        return documentDAO.getResourceOwnerDocuments(resource);
    }

    public void validateDownload(Document document) {
        User user = userService.getCurrentUser();
        Resource resource = document.getResource();
        if (!user.getId().equals(document.getUser().getId())) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            if (viewEditAction == null || !actionService.checkActionAvailable(resource, viewEditAction, user, false)) {
                throw new WorkflowPermissionException(resource, viewEditAction);
            }
        }
    }

    public byte[] getDocumentContent(Integer document) throws IntegrationException {
        return getDocumentContent(getById(document));
    }

    public byte[] getDocumentContent(Document document) throws IntegrationException {
        if (document.getExported()) {
            String amazonObjectKey = document.getExportFilenameAmazon();

            try {
                return getAmazonObjectData(amazonBucket, amazonObjectKey);
            } catch (AmazonS3Exception e1) {
                if (!contextEnvironment.equals("prod")) {
                    try {
                        getAmazonClient().copyObject(amazonProductionBucket, amazonObjectKey, amazonBucket, amazonObjectKey);
                        return getAmazonObjectData(amazonBucket, amazonObjectKey);
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

    public void exportDocumentToAmazon(Integer documentId) {
        Document document = getById(documentId);
        if (!document.getExported()) {

            ObjectMetadata amazonMetadata = new ObjectMetadata();
            amazonMetadata.setContentType(document.getContentType());
            byte[] content = document.getContent();
            amazonMetadata.setContentLength(content.length);

            ByteArrayInputStream amazonStream = null;

            try {
                amazonStream = new ByteArrayInputStream(content);
                PutObjectRequest amazonRequest = new PutObjectRequest(amazonBucket, document.getExportFilenameAmazon(), amazonStream, amazonMetadata);
                getAmazonClient().putObject(amazonRequest);
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
            ListObjectsRequest amazonRequest = new ListObjectsRequest().withBucketName(amazonBucket);
            ObjectListing amazonObjects = getAmazonClient().listObjects(amazonRequest);

            for (S3ObjectSummary amazonObject : amazonObjects.getObjectSummaries()) {
                String amazonObjectKey = amazonObject.getKey();
                Document document = getById(Integer.parseInt(amazonObjectKey));

                if (document == null) {
                    getAmazonClient().deleteObject(amazonBucket, amazonObjectKey);
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

    public byte[] getAmazonObjectData(String bucketName, String amazonObjectKey) {
        S3Object amazonObject = getAmazonObject(bucketName, amazonObjectKey);

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

    public S3Object getAmazonObject(String bucketName, String amazonObjectKey) {
        GetObjectRequest amazonRequest = new GetObjectRequest(bucketName, amazonObjectKey);
        return getAmazonClient().getObject(amazonRequest);
    }

    public AmazonS3 getAmazonClient() throws IntegrationException {
        return new AmazonS3Client(systemService.getAmazonCredentials());
    }

    public Document cloneDocument(Document oldDocument) {
        if (oldDocument != null) {
            Document newDocument = new Document();
            newDocument.setContentType(oldDocument.getContentType());
            newDocument.setFileName(oldDocument.getFileName());
            newDocument.setCategory(oldDocument.getCategory());
            newDocument.setContent(getDocumentContent(oldDocument));
            newDocument.setUser(oldDocument.getUser());
            newDocument.setCreatedTimestamp(new DateTime());
            newDocument.setExported(false);
            return newDocument;
        }
        return null;
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
