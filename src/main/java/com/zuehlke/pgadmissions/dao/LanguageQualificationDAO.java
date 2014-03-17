package com.zuehlke.pgadmissions.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.LanguageQualification;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Repository
public class LanguageQualificationDAO {

    private final SessionFactory sessionFactory;

    private final EncryptionHelper encryptionHelper;
    
    public LanguageQualificationDAO() {
        this(null, null);
    }

    @Autowired
    public LanguageQualificationDAO(SessionFactory sessionFactory, EncryptionHelper encryptionHelper) {
        this.sessionFactory = sessionFactory;
        this.encryptionHelper = encryptionHelper;
    }

    public LanguageQualification getLanguageQualificationById(Integer id) {
        return (LanguageQualification) sessionFactory.getCurrentSession().get(LanguageQualification.class, id);
    }
    
    public LanguageQualification getLanguageQualificationByEncryptedId(String id) {
        return (LanguageQualification) sessionFactory.getCurrentSession().get(LanguageQualification.class, encryptionHelper.decryptToInteger(id));
    }

    public void save(LanguageQualification languageQualification) {
        sessionFactory.getCurrentSession().saveOrUpdate(languageQualification);
    }
    
}
