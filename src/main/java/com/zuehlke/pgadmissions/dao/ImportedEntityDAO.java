package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.ImportedEntity;
import com.zuehlke.pgadmissions.domain.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.Institution;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

@Repository
@SuppressWarnings("unchecked")
public class ImportedEntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends ImportedEntity> T getById(Integer id) {
        return (T) sessionFactory.getCurrentSession().get(ImportedEntity.class, id);
    }
    
    public <T extends ImportedEntity> T getImportedEntityByCode(Class<? extends ImportedEntity> entityClass, Institution institution, String code) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }

    public <T extends ImportedEntity> T getImportedEntityByName(Class<? extends ImportedEntity> entityClass, Institution institution, String name) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }
    
    public ImportedInstitution getImportedInstitutionByCode(Domicile domicile, String code) {
        return (ImportedInstitution) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }
    
    public ImportedInstitution getImportedInstitutionByName(Domicile domicile, String name) {
        return (ImportedInstitution) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }
    
    public <T extends ImportedEntity> List<T> getEnabledImportedEntities(Institution institution, Class<T> entityClass) {
        return sessionFactory.getCurrentSession().createCriteria(entityClass)//
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name"))
                .list();
    }
    
    public List<ImportedInstitution> getEnabledImportedInstitutions(Domicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class)//
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name"))
                .list();
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED)) //
                .addOrder(Order.asc("institution")) //
                .addOrder(Order.asc("importedEntityType")) //
                .list();
    }

    public void save(ImportedEntity entity) {
        sessionFactory.getCurrentSession().save(entity);
    }

    public void update(ImportedEntity entity) {
        sessionFactory.getCurrentSession().update(entity);
    }
    
    public void disableAllEntities(Class<?> entityClass) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + entityClass.getSimpleName() + " " //
                        + "set enabled = false") //
                .executeUpdate();
    }

    public void disableAllEntities(Class<?> entityClass, Institution institution) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + entityClass.getSimpleName() + " " //
                    + "set enabled = false "
                    + "where institution = :institution") //
                .setParameter("institution", institution) // 
                .executeUpdate();
    }
    
    public void disableAllImportedPrograms(Institution institution, LocalDate baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Program "
                    + "set dueDate = :dueDate "
                    + "where institution = :institution "
                        + "and imported is true")
                .setParameter("institution", institution)
                .setParameter("dueDate", baseline)
                .executeUpdate();
    }
    
    public void disableAllImportedProgramStudyOptions(Institution institution) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ProgramStudyOption " //
                    + "set enabled = false, "
                        + "defaultStartDate = null " //
                    + "where program in (" //
                        + "select id "
                            + "from Program " //
                        + "where institution = :institution "
                            + "and imported is true)") //
                .setParameter("institution", institution) //
                .executeUpdate();
    }

    public void disableAllImportedProgramStudyOptionInstances(Institution institution) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ProgramStudyOptionInstance " //
                    + "set enabled = false " //
                    + "where programStudyOption in (" //
                        + "select programStudyOption.id "
                            + "from Program join programStudyOptions programStudyOption "
                        + "where institution = :institution "
                            + "and imported is true)") //
                .setParameter("institution", institution) //
                .executeUpdate();
        
    }
    
}
