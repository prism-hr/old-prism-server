package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.Institution;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class ImportedEntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends ImportedEntity> List<T> getImportedEntities(Class<T> clazz) {
        return sessionFactory.getCurrentSession().createCriteria(clazz)//
                .addOrder(Order.asc("name")).list();
    }

    public <T extends ImportedEntity> T getById(Integer id) {
        return (T) sessionFactory.getCurrentSession().get(ImportedEntity.class, id);
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class).addOrder(Order.asc("id")).list();
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

    public void disableAllEntities(Class<? extends ImportedEntity> entityClass) {
        sessionFactory.getCurrentSession().createQuery("update " + entityClass.getSimpleName() + " set enabled = false") //
                .executeUpdate();
    }

    public void disableAllProgramInstances(Institution institution) {
        sessionFactory.getCurrentSession().createQuery("update ProgramInstance as pi set enabled = false where pi.program in (select id from Program where institution = :institution)") //
                .setParameter("institution", institution)//
                .executeUpdate();
    }
}
