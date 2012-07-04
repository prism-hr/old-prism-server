package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;

@Service
public class DocumentService {

	private final DocumentDAO documentDAO;
	private final ApplicationFormDAO applicationFormDAO;

	DocumentService() {
		this(null, null);
	}

	@Autowired
	public DocumentService(DocumentDAO documentDAO, ApplicationFormDAO applicationFormDAO) {
		this.documentDAO = documentDAO;
		this.applicationFormDAO = applicationFormDAO;

	}

	@Transactional
	public void save(Document document) {
		documentDAO.save(document);
	}

	public Document getDocumentById(Integer id) {
		return documentDAO.getDocumentbyId(id);
	}

	@Transactional
	public void delete(Document document) {
		documentDAO.deleteDocument(document);

	}

	@Transactional
	public void deletePersonalStatement(ApplicationForm applicationForm) {
		Document personalStatement = applicationForm.getPersonalStatement();
		applicationForm.setPersonalStatement(null);		
		applicationFormDAO.save(applicationForm);
		if(personalStatement != null){
			documentDAO.deleteDocument(personalStatement);
		}

	}

	@Transactional
	public void deleteCV(ApplicationForm applicationForm) {
		Document cv = applicationForm.getCv();
		applicationForm.setCv(null);
		applicationFormDAO.save(applicationForm);
		if(cv != null){
			documentDAO.deleteDocument(cv);
		}

	}
}
