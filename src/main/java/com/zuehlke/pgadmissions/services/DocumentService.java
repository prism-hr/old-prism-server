package com.zuehlke.pgadmissions.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.enums.DocumentType;

@Service
@Transactional
public class DocumentService {
    
    @Autowired
    private DocumentDAO documentDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityService entityService;

    @Transactional(readOnly = true)
    public Document getByid(Integer id) {
        return entityService.getById(Document.class, id);
    }

    public Document create(MultipartFile multipartFile, DocumentType documentType) throws IOException {
        if (multipartFile == null) {
            return null;
        }
        
        Document document = new Document();
        document.setFileName(multipartFile.getOriginalFilename());
        document.setContentType(multipartFile.getContentType());
        document.setContent(multipartFile.getBytes());
        document.setType(documentType);
        document.setFileData(multipartFile);
        
        return document;
    }

    public void save(Document document) {
        entityService.save(document);
    }

    public void deleteOrphanDocuments() {
        documentDAO.deleteOrphanDocuments();
    }

    public void replaceDocument(Document oldDocument, Document newDocument) {
        if (oldDocument != null) {
            oldDocument.setIsReferenced(false);
        }
        if (newDocument != null) {
            newDocument.setIsReferenced(true);
        }
    }

}
