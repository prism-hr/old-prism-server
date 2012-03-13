package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.LanguageDAO;
import com.zuehlke.pgadmissions.domain.Language;

@Service
public class LanguageService {

	private final LanguageDAO languageDAO;
	LanguageService(){
		this(null);
	}
	@Autowired
	public LanguageService(LanguageDAO languageDAO) {
		this.languageDAO = languageDAO;		
	}
	

	public List<Language> getAllLanguages() {
		return languageDAO.getAllLanguages();
	}
	

	public Language getLanguageById(Integer id) {
		return languageDAO.getLanguageById(id);
	}

}
