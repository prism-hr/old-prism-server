package com.zuehlke.pgadmissions.services;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.FileCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.system.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.exceptions.PrismBadRequestException;

@Service
@Transactional
public class DocumentService {

    @Value("${amazon.bucket}")
    private String amazonBucket;

    @Value("${amazon.access.key.id}")
    private String amazonAccessKeyId;

    @Value("${amazon.secret.access.key}")
    private String amazonSecretAccessKey;

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
        return entityService.getByProperties(Document.class, ImmutableMap.<String, Object> of("id", id, "category", category));
    }

    public Document create(FileCategory category, Part uploadStream) throws IOException {
        return create(category, getFileName(uploadStream), Streams.readAll(uploadStream.getInputStream()), uploadStream.getContentType());
    }

    public Document create(FileCategory category, String fileName, byte[] content, String contentType) throws IOException {
        Preconditions.checkNotNull(category);

        if (category == FileCategory.IMAGE) {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if (image == null) {
                throw new PrismBadRequestException("Uploaded file is not valid image file");
            }
            image = Scalr.resize(image, Scalr.Mode.AUTOMATIC, 340, 240);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, contentType.replaceAll("image/", ""), baos);
            baos.flush();
            content = baos.toByteArray();
            baos.close();
            contentType = "image/jpeg";
        } else if (category == FileCategory.DOCUMENT) {
            PdfReader pdfReader = null;
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

    public void deleteOrphanDocuments() throws IOException {
        DateTime baseline = new DateTime();
        documentDAO.deleteOrphanDocuments(baseline);
        
        System system = systemService.getSystem();
        if (system.getLastAmazonCleanupDate().isBefore(baseline.toLocalDate())) {   
            cleanupAmazon();
        }
    }

    public void validateDownload(Document document) {
        User user = userService.getCurrentUser();
        Resource resource = document.getResource();

        if (resource == null || (document.getUserPortrait() != null && !document.getUserPortrait().isEnabled())) {
            throw new AccessDeniedException("Document unavailable");
        }

        if (resource != null) {
            Action viewEditAction = actionService.getViewEditAction(resource);
            actionService.validateUserAction(resource, viewEditAction, user);
        }
    }

    public byte[] getContent(Document document) throws IOException {
        if (document.getExported() == true) {
            AmazonS3 amazonClient = getAmazonClient();
            GetObjectRequest amazonRequest = new GetObjectRequest(amazonBucket, getAmazonObjectKey(document));
            S3Object amazonObject = amazonClient.getObject(amazonRequest);
            return IOUtils.toByteArray(amazonObject.getObjectContent());
        }
        return document.getContent();
    }

    public List<Integer> getDocumentsForExport() {
        return documentDAO.getDocumentsForExport();
    }

    public void exportDocument(Integer documentId) throws IOException {
        Document document = getById(documentId);
        AmazonS3 amazonClient = getAmazonClient();

        ObjectMetadata amazonMetadata = new ObjectMetadata();
        amazonMetadata.setContentType(document.getContentType());
        byte[] content = document.getContent();
        amazonMetadata.setContentLength(content.length);
        PutObjectRequest amazonRequest = new PutObjectRequest(amazonBucket, getAmazonObjectKey(document), new ByteArrayInputStream(content), amazonMetadata);
        amazonClient.putObject(amazonRequest);

        document.setContent(null);
        document.setExported(true);
    }

    private String getAmazonObjectKey(Document document) {
        return String.format("%10d", document.getId());
    }
    
    private void cleanupAmazon() throws IOException {
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
    }
    
    private AmazonS3 getAmazonClient() throws IOException {
        System system = systemService.getSystem();

        Properties amazonCredentials = new Properties();
        amazonCredentials.setProperty("amazonKey", system.getAmazonAccessKey());
        amazonCredentials.setProperty("secretKey", system.getAmazonSecretKey());

        ByteArrayOutputStream amazonCredentialsOutputStream = new ByteArrayOutputStream();
        amazonCredentials.store(amazonCredentialsOutputStream, null);
        ByteArrayInputStream amazonCredentialsInputStream = new ByteArrayInputStream(amazonCredentialsOutputStream.toByteArray());

        return new AmazonS3Client(new PropertiesCredentials(amazonCredentialsInputStream));
    }

    private String getFileName(Part upload) {
        for (String cd : upload.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
