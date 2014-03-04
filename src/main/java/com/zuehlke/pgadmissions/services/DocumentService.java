package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Qualification;

@Service
@Transactional
public class DocumentService {

    private final DocumentDAO documentDAO;
    private final ApplicationFormDAO applicationFormDAO;
    private final QualificationDAO qualificationDAO;

    public DocumentService() {
        this(null, null, null);
    }

    @Autowired
    public DocumentService(DocumentDAO documentDAO, ApplicationFormDAO applicationFormDAO, QualificationDAO qualificationDAO) {
        this.documentDAO = documentDAO;
        this.applicationFormDAO = applicationFormDAO;
        this.qualificationDAO = qualificationDAO;
    }

    public void save(Document document) {
        documentDAO.save(document);
    }

    public Document getDocumentById(Integer id) {
        return documentDAO.getDocumentbyId(id);
    }

    public void delete(Document document) {
        documentDAO.deleteDocument(document);
    }

    public void deletePersonalStatement(ApplicationForm applicationForm) {
        Document personalStatement = applicationForm.getPersonalStatement();
        applicationForm.setPersonalStatement(null);
        applicationFormDAO.save(applicationForm);
        if (personalStatement != null) {
            documentDAO.deleteDocument(personalStatement);
        }
    }

    public void deleteCV(ApplicationForm applicationForm) {
        Document cv = applicationForm.getCv();
        applicationForm.setCv(null);
        applicationFormDAO.save(applicationForm);
        if (cv != null) {
            documentDAO.deleteDocument(cv);
        }

    }

    public void deleteQualificationProofOfAward(Qualification qualification) {
        Document proofOfAward = qualification.getProofOfAward();
        qualification.setProofOfAward(null);
        qualificationDAO.save(qualification);
        if (proofOfAward != null) {
            documentDAO.deleteDocument(proofOfAward);
        }
    }
    
    public void deleteOrphanDocuments() {
        documentDAO.deleteOrphanDocuments();
    }
    
}
