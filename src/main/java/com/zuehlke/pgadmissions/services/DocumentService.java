package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public void save(Document document) {
		documentDAO.save(document);
		
	}

}
