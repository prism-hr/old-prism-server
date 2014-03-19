package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.LanguageQualificationDAO;
import com.zuehlke.pgadmissions.domain.LanguageQualification;

@Service
@Transactional
public class LanguageQualificationService {

    private final LanguageQualificationDAO languageQualificationDAO;

    public LanguageQualificationService() {
        this(null);
    }

    @Autowired
    public LanguageQualificationService(LanguageQualificationDAO languageQualificationDAO) {
        this.languageQualificationDAO = languageQualificationDAO;
    }
    
    public LanguageQualification getLanguageQualificationById(Integer id) {
        return languageQualificationDAO.getLanguageQualificationById(id);
    }
    
    public LanguageQualification getLanguageQualificationByEncryptedId(String id) {
        return languageQualificationDAO.getLanguageQualificationByEncryptedId(id);
    }

    public void save(LanguageQualification languageQualification) {
        languageQualificationDAO.save(languageQualification);
    }
    
}
