package com.zuehlke.pgadmissions.services;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.io.Streams;
import org.imgscalr.Scalr;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.PropertiesCredentials;
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
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.FileCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.exceptions.IntegrationException;
import com.zuehlke.pgadmissions.exceptions.PrismBadRequestException;

@Service
@Transactional
public class DocumentService {

    @Value("${context.environment}")
    private String contextEnvironment;

    @Value("${integration.amazon.bucket}")
    private String amazonBucket;

    @Value("${integration.amazon.production.bucket}")
    private String amazonProductionBucket;

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    public Document getById(Integer id) {
        return entityService.getById(Document.class, id);
    }

    public Document getById(Integer id, FileCategory category) {
        return entityService.getByProperties(Document.class, ImmutableMap.<String, Object>of("id", id, "category", category));
    }

    public Document create(FileCategory category, Part uploadStream) throws IOException {
        return create(category, getFileName(uploadStream), Streams.readAll(uploadStream.getInputStream()), uploadStream.getContentType());
    }

    public Document create(FileCategory category, String fileName, byte[] content, String contentType) throws IOException {
        Preconditions.checkNotNull(category);

        if (category == FileCategory.IMAGE) {
            content = createImageDocument(content, contentType);
            contentType = "image/jpeg";
        } else if (category == FileCategory.DOCUMENT) {
            createPdfDocument(content);
        }
        Document document = new Document().withContent(content).withContentType(contentType).withExported(false).withFileName(fileName)
                .withUser(userService.getCurrentUser()).withCreatedTimestamp(new DateTime()).withCategory(category);
        entityService.save(document);
        return document;
    }

    public Document getExternalFile(FileCategory fileCategory, String documentLink) throws IOException {
        URL logoDocumentUri = new DefaultResourceLoader().getResource(documentLink).getURL();
        URLConnection connection = logoDocumentUri.openConnection();
        InputStream stream = connection.getInputStream();
        byte[] content = IOUtils.toByteArray(stream);
        String contentType = connection.getContentType();
        String fileName = FilenameUtils.getName(documentLink);
        return create(fileCategory, fileName, content, contentType);
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

        if (resource == null || (document.getUserPortrait() != null && !document.getUserPortrait().isEnabled())) {
            throw new AccessDeniedException("Document unavailable");
        }

        Action viewEditAction = actionService.getViewEditAction(resource);
        actionService.validateUserAction(resource, viewEditAction, user);
    }

    public byte[] getDocumentContent(Document document) throws IOException, IntegrationException {
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
                    } catch (AmazonS3Exception e2) {
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

    public List<Integer> getOrphanDocuments(DateTime baselineTime) {
        return documentDAO.getOrphanDocuments(baselineTime);
    }

    public void deleteDocument(Integer documentId) {
        Document document = getById(documentId);
        entityService.delete(document);
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

    public byte[] getSystemDocument(String path) throws IOException {
        return Resources.toByteArray(Resources.getResource(path));
    }
    
    private byte[] createImageDocument(byte[] content, String contentType) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
        if (image == null) {
            throw new PrismBadRequestException("Uploaded file is not valid image file");
        }

        final int SIZE = 600;
        final int HALF_SIZE = SIZE / 2;
        image = Scalr.resize(image, Scalr.Mode.AUTOMATIC, SIZE, SIZE);

        BufferedImage paddedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = paddedImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, SIZE, SIZE);
        graphics.drawImage(image, HALF_SIZE - image.getWidth() / 2, HALF_SIZE - image.getHeight() / 2, null);
        graphics.dispose();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(paddedImage, contentType.replaceAll("image/", ""), baos);
        baos.flush();
        content = baos.toByteArray();
        baos.close();
        return content;
    }
    
    private void createPdfDocument(byte[] content) {
        PdfReader pdfReader;
        try {
            pdfReader = new PdfReader(content);
        } catch (IOException e) {
            throw new PrismBadRequestException("Uploaded file is not valid PDF file");
        }

        long permissions = pdfReader.getPermissions();
        if (pdfReader.isEncrypted() && (permissions & PdfWriter.ALLOW_COPY) == 0) {
            throw new PrismBadRequestException("You cannot upload an encrypted file");
        }
    }

    private AmazonS3 getAmazonClient() throws IOException, IntegrationException {
        System system = systemService.getSystem();
        Properties amazonProperties = getAmazonProperties(system);

        ByteArrayOutputStream amazonCredentialsOutputStream = null;
        ByteArrayInputStream amazonCredentialsInputStream = null;

        try {
            amazonCredentialsOutputStream = new ByteArrayOutputStream();
            amazonProperties.store(amazonCredentialsOutputStream, null);
            amazonCredentialsInputStream = new ByteArrayInputStream(amazonCredentialsOutputStream.toByteArray());

            PropertiesCredentials amazonCredentials = new PropertiesCredentials(amazonCredentialsInputStream);

            IOUtils.closeQuietly(amazonCredentialsOutputStream);
            IOUtils.closeQuietly(amazonCredentialsInputStream);

            return new AmazonS3Client(amazonCredentials);
        } finally {
            IOUtils.closeQuietly(amazonCredentialsOutputStream);
            IOUtils.closeQuietly(amazonCredentialsInputStream);
        }
    }

    private Properties getAmazonProperties(System system) throws IntegrationException {
        String accessKey = system.getAmazonAccessKey();
        String secretKey = system.getAmazonSecretKey();
        
        if (accessKey == null || secretKey == null) {
            throw new IntegrationException("Amazon credentials not in database");
        }
         
        Properties amazonProperties = new Properties();
        amazonProperties.setProperty("accessKey", accessKey);
        amazonProperties.setProperty("secretKey", secretKey);
        return amazonProperties;
    }

    private byte[] getAmazonObject(AmazonS3 amazonClient, String amazonObjectKey) throws IOException {
        GetObjectRequest amazonRequest = new GetObjectRequest(amazonBucket, amazonObjectKey);
        S3Object amazonObject = amazonClient.getObject(amazonRequest);

        S3ObjectInputStream amazonStream = null;
        try {
            amazonStream = amazonObject.getObjectContent();
            byte[] amazonByteArray = IOUtils.toByteArray(amazonStream);
            IOUtils.closeQuietly(amazonStream);
            return amazonByteArray;
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

    private byte[] getFallbackDocument(Document document) throws IOException {
        return getSystemDocument("document/" + (document.getCategory() == FileCategory.DOCUMENT ? "document_exported.pdf" : "image_exported.jpg"));
    }

}
