package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ImportedObject;

@Repository
public class ImportedDataDAO {

    private final SessionFactory sessionFactory;

    ImportedDataDAO() {
        this(null);
    }

    @Autowired
    public ImportedDataDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;

    }

    @SuppressWarnings("unchecked")
    public List<ImportedObject> getDisabledImportedObjectsWithoutActiveReference(Class<? extends ImportedObject> importedType) {
        return sessionFactory.getCurrentSession().createCriteria(importedType) //
                .add(Restrictions.eq("enabled", false)) //
                .add(Restrictions.isNull("enabledObject")).list();
    }

    public ImportedObject getEnabledVersion(ImportedObject disabledObject) {
        return (ImportedObject) sessionFactory.getCurrentSession().createCriteria(disabledObject.getClass()) //
                .add(Restrictions.eq("code", disabledObject.getCode())) //
                .add(Restrictions.eq("enabled", true)).uniqueResult();
    }

}