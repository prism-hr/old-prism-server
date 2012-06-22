package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.DocumentDAO;
import com.zuehlke.pgadmissions.domain.Document;

@Service
public class DocumentService {

	private final DocumentDAO documentDAO;
	DocumentService(){
		this(null);
	}
	@Autowired
	public DocumentService(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;

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

}
