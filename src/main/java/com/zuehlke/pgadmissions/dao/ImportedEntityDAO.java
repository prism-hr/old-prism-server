package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.List;

import org.hibernate.Criteria;
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
import com.zuehlke.pgadmissions.domain.imported.AgeRange;
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

    public List<ImportedEntityFeed> getImportedEntityFeeds(Integer institution, PrismImportedEntity... exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution.id", institution)) //
                .add(Restrictions.eq("resourceState.state.id", INSTITUTION_APPROVED));

        for (PrismImportedEntity exclusion : exclusions) {
            criteria.add(Restrictions.ne("importedEntityType", exclusion));
        }

        return criteria.addOrder(Order.asc("importedEntityType")) //
                .list();
    }

    public void save(ImportedEntity entity) {
        sessionFactory.getCurrentSession().save(entity);
    }

    public void update(ImportedEntity entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    public void disableImportedEntities(Class<?> entityClass, Institution institution) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + entityClass.getSimpleName() + " " //
                        + "set enabled = false " //
                        + "where institution = :institution") //
                .setParameter("institution", institution) //
                .executeUpdate();
    }

    public void disableImportedInstitutions(Institution institution) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ImportedInstitution " //
                        + "set enabled = false " //
                        + "where institution = :institution " //
                        + "and custom is false") //
                .setParameter("institution", institution) //
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
                "delete ResourceStudyOption " //
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
                "delete ResourceStudyOptionInstance " //
                        + "where studyOption in (" //
                        + "select resourceStudyOption.id " //
                        + "from ResourceStudyOption as resourceStudyOption " //
                        + "join resourceStudyOption.program as program " //
                        + "where program.institution = :institution " //
                        + "and program.imported is true "
                        + "and program.id not in (:updates))") //
                .setParameter("institution", institution) //
                .setParameterList("updates", updates) //
                .executeUpdate();

    }

    public void mergeImportedEntities(String table, String columns, String values) {
        sessionFactory.getCurrentSession().createSQLQuery(
                "insert into " + table + " (" + columns + ") "
                        + "values " + values + " "
                        + "on duplicate key update enabled = '1'")
                .executeUpdate();
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

    public AgeRange getAgeRange(Institution institution, Integer age) {
        return (AgeRange) sessionFactory.getCurrentSession().createCriteria(AgeRange.class) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.ge("lowerBound", age)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.le("upperBound", age)) //
                        .add(Restrictions.isNull("upperBound"))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

}
