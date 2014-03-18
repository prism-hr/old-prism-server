package com.zuehlke.pgadmissions.services;

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

    public void save(Document document) {
        documentDAO.save(document);
    }

    @Transactional(readOnly = true)
    public Document getDocumentById(Integer id) {
        return documentDAO.getDocumentbyId(id);
    }

    public void deleteOrphanDocuments() {
        documentDAO.deleteOrphanDocuments();
    }

    public void documentReferentialityChanged(Document oldDocument, Document newDocument) {
        if (oldDocument != null) {
            oldDocument.setIsReferenced(false);
        }
        if (newDocument != null) {
            newDocument.setIsReferenced(true);
        }
    }

}
