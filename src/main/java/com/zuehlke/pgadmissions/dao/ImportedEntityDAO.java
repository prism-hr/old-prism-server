package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.imported.Domicile;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.user.Address;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;

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

    public ImportedInstitution getCustomImportedInstitutionByName(Integer domicileId, String name) {
        return (ImportedInstitution) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicile.id", domicileId)) //
                .add(Restrictions.eq("name", name)) //
                .add(Restrictions.eq("custom", true)) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public <T extends ImportedEntity> List<T> getEnabledImportedEntities(Institution institution, Class<T> entityClass) {
        return sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public List<ImportedInstitution> getEnabledImportedInstitutions(Domicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class)//
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public List<ImportedEntityFeed> getImportedEntityFeeds() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.state", "state", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("state.stateGroup.id", PrismStateGroup.INSTITUTION_APPROVED)) //
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

    public void disableEntities(Class<?> entityClass, Institution institution, List<Integer> updates) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + entityClass.getSimpleName() + " " //
                        + "set enabled = false " //
                        + "where institution = :institution "
                        + "and id not in (:updates)") //
                .setParameter("institution", institution) //
                .setParameterList("updates", updates) //
                .executeUpdate();
    }

    public void disableInstitutions(Institution institution, List<Integer> updates) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ImportedInstitution " //
                        + "set enabled = false " //
                        + "where institution = :institution " //
                        + "and custom is false " //
                        + "and id not in (:updates)") //
                .setParameter("institution", institution) //
                .setParameterList("updates", updates) //
                .executeUpdate();
    }

    public void disableImportedPrograms(Institution institution, List<Integer> updates, LocalDate baseline) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Program " //
                        + "set dueDate = :dueDate " //
                        + "where institution = :institution " //
                        + "and imported is true " //
                        + "and id not in (:updates)") //
                .setParameter("institution", institution) //
                .setParameter("dueDate", baseline) //
                .setParameterList("updates", updates) //
                .executeUpdate();
    }

    public void disableImportedProgramStudyOptions(Institution institution, List<Integer> updates) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ProgramStudyOption " //
                        + "set enabled = false " //
                        + "where program in (" //
                        + "select id " //
                        + "from Program " //
                        + "where institution = :institution " //
                        + "and imported is true "
                        + "and id not in (:updates))") //
                .setParameter("institution", institution) //
                .setParameterList("updates", updates) //
                .executeUpdate();
    }

    public void disableImportedProgramStudyOptionInstances(Institution institution, List<Integer> updates) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ProgramStudyOptionInstance " //
                        + "set enabled = false " //
                        + "where studyOption in (" //
                        + "select programStudyOption.id " //
                        + "from ProgramStudyOption as programStudyOption " //
                        + "join programStudyOption.program as program " //
                        + "where program.institution = :institution " //
                        + "and program.imported is true "
                        + "and program.id not in (:updates))") //
                .setParameter("institution", institution) //
                .setParameterList("updates", updates) //
                .executeUpdate();

    }

    public List<Integer> getPendingImportedEntityFeeds(Integer institutionId) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.ne("importedEntityType", PrismImportedEntity.PROGRAM)) //
                .add(Restrictions.isNull("lastImportedTimestamp")) //
                .list();
    }

    public DomicileUseDTO getMostUsedDomicile(Institution institution) {
        return (DomicileUseDTO) sessionFactory.getCurrentSession().createCriteria(Address.class, "address") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("domicile.code"), "code") //
                        .add(Projections.count("id").as("useCount"), "useCount")) //
                .createAlias("domicile", "domicile", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("domicile.institution", institution)) //
                .add(Restrictions.eq("domicile.enabled", true)) //
                .addOrder(Order.desc("useCount")) //
                .setMaxResults(1) //
                .setResultTransformer(Transformers.aliasToBean(DomicileUseDTO.class)) //
                .uniqueResult();
    }

}
