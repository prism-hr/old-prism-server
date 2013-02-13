package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.LanguageQualificationDAO;
import com.zuehlke.pgadmissions.domain.LanguageQualification;

@Service
public class LanguageQualificationService {

    private final LanguageQualificationDAO languageQualificationDAO;

    LanguageQualificationService() {
        this(null);
    }

    @Autowired
    public LanguageQualificationService(LanguageQualificationDAO languageQualificationDAO) {
        this.languageQualificationDAO = languageQualificationDAO;
    }
    
    @Transactional
    public LanguageQualification getLanguageQualificationById(Integer id) {
        return languageQualificationDAO.getLanguageQualificationById(id);
    }
    
    @Transactional
    public LanguageQualification getLanguageQualificationByEncryptedId(String id) {
        return languageQualificationDAO.getLanguageQualificationByEncryptedId(id);
    }

    @Transactional
    public void save(LanguageQualification languageQualification) {
        languageQualificationDAO.save(languageQualification);
    }
    
    @Transactional
    public void deleteAttachedDocument(LanguageQualification languageQualification) {
        languageQualificationDAO.deleteAttachedDocument(languageQualification);
    }
}
