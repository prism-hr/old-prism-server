package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_SUBJECT_AREA;
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

import com.zuehlke.pgadmissions.domain.application.Address;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntitySimpleMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;

@Repository
@SuppressWarnings("unchecked")
public class ImportedEntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

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

    public <T extends ImportedEntity<?>> T getImportedEntityByName(Class<T> entityClass, String name) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public ImportedInstitution getImportedInstitutionByName(Integer domicile, String name) {
        return (ImportedInstitution) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("importedDomicile.id", domicile)) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public ImportedProgram getImportedProgramByName(ImportedInstitution importedInstitution, String name) {
        return (ImportedProgram) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .add(Restrictions.eq("importedInstitution", importedInstitution)) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public <T extends ImportedEntity<?>> List<T> getEnabledImportedEntities(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.RIGHT_OUTER_JOIN); //

        if (prismImportedEntity.getEntityClass().equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq(entityReference + ".type", prismImportedEntity));
        }

        return (List<T>) appendUmappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".name")) //
                .list();
    }

    public <T extends ImportedEntity<?>> List<T> getEnabledImportedEntitiesWithMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference) //
                .createAlias(entityReference, entityReference, JoinType.INNER_JOIN); //

        if (prismImportedEntity.getEntityClass().equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq(entityReference + ".type", prismImportedEntity));
        }

        return (List<T>) appendMappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".name")) //
                .list();
    }

    public List<ImportedInstitution> getEnabledImportedInstitutions(Institution institution, ImportedEntitySimple domicile) {
        PrismImportedEntity prismImportedEntity = IMPORTED_INSTITUTION;
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.RIGHT_OUTER_JOIN) //
                .add(Restrictions.eq(entityReference + ".domicile", domicile)); //

        return (List<ImportedInstitution>) appendUmappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".name")) //
                .list();
    }

    public List<ImportedInstitution> getEnabledImportedInstitutionsWithMappings(Institution institution, ImportedEntitySimple domicile) {
        PrismImportedEntity prismImportedEntity = IMPORTED_INSTITUTION;
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(entityReference + ".domicile", domicile)); //

        return (List<ImportedInstitution>) appendMappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".name")) //
                .list();
    }

    public List<ImportedProgram> getEnabledImportedPrograms(Institution institution, ImportedInstitution importedInstitution) {
        PrismImportedEntity prismImportedEntity = IMPORTED_PROGRAM;
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.RIGHT_OUTER_JOIN) //
                .add(Restrictions.eq(entityReference + ".institution", importedInstitution)); //

        return (List<ImportedProgram>) appendUmappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".name")) //
                .list();
    }

    public List<ImportedProgram> getEnabledImportedProgramsWithMappings(Institution institution, ImportedInstitution importedInstitution) {
        PrismImportedEntity prismImportedEntity = IMPORTED_PROGRAM;
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(entityReference + ".institution", importedInstitution)); //

        return (List<ImportedProgram>) appendMappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".name")) //
                .list();
    }

    public List<ImportedSubjectArea> getEnabledImportedSubjectAreas(Institution institution) {
        PrismImportedEntity prismImportedEntity = IMPORTED_SUBJECT_AREA;
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.RIGHT_OUTER_JOIN); //

        return (List<ImportedSubjectArea>) appendUmappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".code")) //
                .list();
    }

    public List<ImportedSubjectArea> getEnabledImportedSubjectAreasWithMappings(Institution institution) {
        PrismImportedEntity prismImportedEntity = IMPORTED_SUBJECT_AREA;
        String entityReference = prismImportedEntity.getEntityClassLowerCamelName();
        Criteria criteria = getEntitySelectStatement(prismImportedEntity, entityReference)
                .createAlias(entityReference, entityReference, JoinType.INNER_JOIN); //

        return (List<ImportedSubjectArea>) appendMappedEntityRestriction(institution, entityReference, criteria) //
                .addOrder(Order.asc(entityReference + ".code")) //
                .list();
    }

    public <T extends ImportedEntityMapping<?>> List<T> getImportedEntityMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        return getImportedEntityMappings(institution, prismImportedEntity, null);
    }

    public <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> List<V> getEnabledImportedEntityMapping(Institution institution, T importedEntity) {
        return getImportedEntityMapping(institution, importedEntity, true);
    }

    public <T extends ImportedEntityMapping<?>> List<T> getEnabledImportedEntityMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        return getImportedEntityMappings(institution, prismImportedEntity, true);
    }

    public void disableImportedEntities(PrismImportedEntity importedEntity) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + importedEntity.getEntityClass().getSimpleName() + " " //
                        + "set enabled = false") //
                .executeUpdate();
    }

    public void mergeImportedEntities(String table, String columns, String inserts, String updates) {
        executeBulkMerge(table, columns, inserts, updates);
    }

    public <T extends ImportedEntityMapping<?>> void disableImportedEntityMappings(Institution institution, PrismImportedEntity importedEntity) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + importedEntity.getMappingEntityClass().getSimpleName() + " " //
                        + "set enabled = false " //
                        + "where institution = :institution") //
                .setParameter("institution", institution) //
                .executeUpdate();
    }

    public void mergeImportedEntityMappings(String table, String columns, String inserts, String updates) {
        executeBulkMerge(table, columns, inserts, updates);
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

    public DomicileUseDTO getMostUsedDomicile(Institution institution) {
        return (DomicileUseDTO) sessionFactory.getCurrentSession().createCriteria(Address.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("domicile.id"), "id") //
                        .add(Projections.count("id").as("useCount"), "useCount")) //
                .createAlias("domicile", "domicile", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("domicile.institution", institution)) //
                .add(Restrictions.eq("domicile.enabled", true)) //
                .addOrder(Order.desc("useCount")) //
                .setMaxResults(1) //
                .setResultTransformer(Transformers.aliasToBean(DomicileUseDTO.class)) //
                .uniqueResult();
    }

    public ImportedAgeRange getAgeRange(Institution institution, Integer age) {
        return (ImportedAgeRange) sessionFactory.getCurrentSession().createCriteria(ImportedAgeRange.class) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.ge("lowerBound", age)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.le("upperBound", age)) //
                        .add(Restrictions.isNull("upperBound"))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<ImportedInstitution> getInstitutionsWithUcasId() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.isNotNull("ucasId")) //
                .list();
    }

    private <T extends ImportedEntity<V>, V extends ImportedEntityMapping<T>> List<V> getImportedEntityMapping(Institution institution, T importedEntity,
            Boolean enabled) {
        String entityReference = importedEntity.getType().getEntityClassLowerCamelName();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(importedEntity.getType().getMappingEntityClass()) //
                .createAlias(entityReference, entityReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(entityReference + ".type", importedEntity)) //
                .add(Restrictions.eq(entityReference, importedEntity)) //
                .add(Restrictions.eq("institution", institution));

        if (enabled != null) {
            criteria.add(Restrictions.eq("enabled", true));
        }

        return (List<V>) criteria.addOrder(Order.desc("importedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    private <T extends ImportedEntityMapping<?>> List<T> getImportedEntityMappings(Institution institution,
            PrismImportedEntity importedEntity, Boolean enabled) {
        String entityReference = importedEntity.getEntityClassLowerCamelName();
        Class<T> mappingClass = (Class<T>) importedEntity.getMappingEntityClass();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappingClass) //
                .createAlias(entityReference, entityReference, JoinType.INNER_JOIN);

        if (ImportedEntitySimpleMapping.class.isAssignableFrom(mappingClass)) {
            criteria.add(Restrictions.eq(entityReference + ".type", importedEntity));
        }

        criteria.add(Restrictions.eq("institution", institution));

        if (enabled != null) {
            criteria.add(Restrictions.eq("enabled", enabled));
        }

        return (List<T>) criteria.addOrder(Order.desc("importedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    private void executeBulkMerge(String table, String columns, String inserts, String updates) {
        sessionFactory.getCurrentSession().createSQLQuery(
                "insert into " + table + " (" + columns + ") "
                        + "values " + inserts + " "
                        + "on duplicate key update " + updates)
                .executeUpdate();
    }

    private Criteria getEntitySelectStatement(PrismImportedEntity prismImportedEntity, String entityReference) {
        return sessionFactory.getCurrentSession().createCriteria(prismImportedEntity.getMappingEntityClass()) //
                .setProjection(Projections.groupProperty(entityReference));
    }

    private Criteria appendUmappedEntityRestriction(Institution institution, String entityReference, Criteria criteria) {
        return criteria.add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq(entityReference + ".enabled", true)) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNull("code")) //
                                .add(Restrictions.eq("enabled", true))));
    }

    private Criteria appendMappedEntityRestriction(Institution institution, String entityReference, Criteria criteria) {
        return criteria.add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.isNotNull("code")) //
                .add(Restrictions.eq("enabled", true)); //
    }

}
