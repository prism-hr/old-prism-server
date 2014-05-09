package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;

@Repository
@SuppressWarnings("unchecked")
public class ImportedEntityDAO {

    private final SessionFactory sessionFactory;

    public ImportedEntityDAO() {
        this(null);
    }

    @Autowired
    public ImportedEntityDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T extends ImportedEntity> List<T> getImportedEntities(Class<T> clazz) {
        return sessionFactory.getCurrentSession().createCriteria(clazz)//
                .addOrder(Order.asc("name")).list();
    }

    public <T extends ImportedEntity> T getById(Integer id) {
        return (T) sessionFactory.getCurrentSession().get(ImportedEntity.class, id);
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class).list();
    }

    public <T extends ImportedEntity> T getByCode(Class<? extends ImportedEntity> clazz, String code) {
        return (T) sessionFactory.getCurrentSession().createCriteria(clazz) //
                .add(Restrictions.eq("code", code)).uniqueResult();
    }

    public <T extends ImportedEntity> T getByName(Class<? extends ImportedEntity> clazz, String name) {
        return (T) sessionFactory.getCurrentSession().createCriteria(clazz) //
                .add(Restrictions.eq("name", name)).uniqueResult();
    }

    public void save(ImportedEntity entity) {
        sessionFactory.getCurrentSession().save(entity);
    }

    public void update(ImportedEntity entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    public void flushSession() {
        sessionFactory.getCurrentSession().flush();
    }

    public boolean attemptUpdateByCode(ImportedEntity entity) {
        return sessionFactory.getCurrentSession().createQuery("update :entityType set name = :name where code = :code")//
                .setString("entityType", entity.getClass().getSimpleName()) //
                .setString("name", entity.getName()) //
                .setString("code", entity.getCode()) //
                .executeUpdate() > 0;

    }
}
