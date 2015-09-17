package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

@Repository
@SuppressWarnings("unchecked")
public class ImportedEntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends ImportedEntity<?>> T getByName(Class<T> entityClass, String name) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public <T extends ImportedEntity<?>> List<T> searchByName(Class<T> entityClass, String searchTerm) {
        return sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE)) //
                .list();
    }

    public <T extends ImportedEntity<?>> List<T> getSimilarImportedEntities(Class<T> entityClass, String searchTerm) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(entityClass)
                .add(Restrictions.like("name", searchTerm, MatchMode.ANYWHERE))
                .add(Restrictions.eq("enabled", true))
                .list();
    }

    public <T extends ImportedEntity<?>> List<T> getImportedEntities(PrismImportedEntity prismImportedEntity) {
        Class<?> entityClass = prismImportedEntity.getEntityClass();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);

        if (entityClass.equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq("type", prismImportedEntity));
        }

        return (List<T>) criteria.list();
    }

    public <T extends ImportedEntity<?>> List<T> getEnabledImportedEntities(PrismImportedEntity prismImportedEntity) {
        Class<?> entityClass = prismImportedEntity.getEntityClass();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);

        if (entityClass.equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq("type", prismImportedEntity));
        }

        return (List<T>) criteria.add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public void disableImportedEntities(PrismImportedEntity prismImportedEntity) {
        String queryString = "update " + prismImportedEntity.getEntityClass().getSimpleName() + " " //
                + "set enabled = false";

        boolean simpleEntity = prismImportedEntity.getEntityClass().equals(ImportedEntitySimple.class);
        if (simpleEntity) {
            queryString = queryString + " where type = :type";
        }

        Query query = sessionFactory.getCurrentSession().createQuery(queryString);

        if (simpleEntity) {
            query.setParameter("type", prismImportedEntity);
        }

        query.executeUpdate();
    }

    public ImportedAgeRange getAgeRange(Integer age) {
        return (ImportedAgeRange) sessionFactory.getCurrentSession().createCriteria(ImportedAgeRange.class) //
                .createAlias("mappings", "ageMapping", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("ageMapping.enabled", true))
                .add(Restrictions.ge("lowerBound", age)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.le("upperBound", age)) //
                        .add(Restrictions.isNull("upperBound"))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public void deleteImportedEntityTypes() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ImportedEntityType") //
                .executeUpdate();
    }

}
