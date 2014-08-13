package com.zuehlke.pgadmissions.services;

import java.io.IOException;

import javax.servlet.http.Part;

import org.bouncycastle.util.io.Streams;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.Document;

@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserService userService;

    public Document getByid(Integer id) {
        return entityService.getById(Document.class, id);
    }

    public Document create(Part uploadStream) throws IOException {
        Document document = new Document().withContent(Streams.readAll(uploadStream.getInputStream())).withContentType(uploadStream.getContentType())
                .withFileName(getFileName(uploadStream)).withUser(userService.getCurrentUser()).withCreatedTimestamp(new DateTime());
        entityService.save(document);
        return document;
    }

    public void deleteOrphanDocuments() {
        documentDAO.deleteOrphanDocuments();
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
