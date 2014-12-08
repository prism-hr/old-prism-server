package com.zuehlke.pgadmissions.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.document.FileCategory;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Action;
import com.zuehlke.pgadmissions.exceptions.PrismBadRequestException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.io.Streams;
import org.imgscalr.Scalr;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private ActionService actionService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public Document getById(Integer id, FileCategory category) {
        return entityService.getByProperties(Document.class, ImmutableMap.<String, Object>of("id", id, "category", category));
    }

    public Document create(FileCategory category, Part uploadStream) throws IOException {
        return create(category, getFileName(uploadStream), Streams.readAll(uploadStream.getInputStream()), uploadStream.getContentType());
    }

    public Document create(FileCategory category, String fileName, byte[] content, String contentType) throws IOException {
        Preconditions.checkNotNull(category);

        if (category == FileCategory.IMAGE) {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if(image == null){
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
        Document document = new Document().withContent(content).withContentType(contentType).withFileName(fileName).withUser(userService.getCurrentUser())
                .withCreatedTimestamp(new DateTime()).withCategory(category);
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

    public void deleteOrphanDocuments() {
        documentDAO.deleteOrphanDocuments();
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

    private String getFileName(Part upload) {
        for (String cd : upload.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

}
