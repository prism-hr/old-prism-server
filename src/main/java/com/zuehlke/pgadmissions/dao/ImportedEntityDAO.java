package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.zuehlke.pgadmissions.domain.address.AddressApplication;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedAgeRange;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectAreaDTO;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgramSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.WeightedRelationImported;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntitySimpleMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;
import com.zuehlke.pgadmissions.dto.ImportedProgramDTO;
import com.zuehlke.pgadmissions.dto.ImportedSubjectAreaDTO;

@Repository
@SuppressWarnings("unchecked")
public class ImportedEntityDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public <T extends ImportedEntity<?, ?>> T getByName(Class<T> entityClass, String name) {
        return (T) sessionFactory.getCurrentSession().createCriteria(entityClass) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public ImportedInstitution getImportedInstitutionByName(Integer domicile, String name) {
        return (ImportedInstitution) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicile.id", domicile)) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public ImportedProgram getImportedProgramByName(ImportedInstitution institution, String name) {
        return (ImportedProgram) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("name", name)) //
                .uniqueResult();
    }

    public <T extends ImportedEntity<?, ?>> List<T> getImportedEntities(PrismImportedEntity prismImportedEntity) {
        Class<?> entityClass = prismImportedEntity.getEntityClass();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);

        if (entityClass.equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq("type", prismImportedEntity));
        }

        return (List<T>) criteria.list();
    }

    public <T extends ImportedEntity<?, ?>> List<T> getEnabledImportedEntities(PrismImportedEntity prismImportedEntity) {
        Class<?> entityClass = prismImportedEntity.getEntityClass();
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);

        if (entityClass.equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq("type", prismImportedEntity));
        }

        return (List<T>) criteria.add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public <T extends ImportedEntity<?, ?>> List<T> getEnabledImportedEntitiesWithMappings(Institution institution, PrismImportedEntity prismImportedEntity) {
        Criteria criteria = getEntityMappingSelectStatement(prismImportedEntity) //
                .createAlias("importedEntity", "importedEntity", JoinType.INNER_JOIN); //

        if (prismImportedEntity.getEntityClass().equals(ImportedEntitySimple.class)) {
            criteria.add(Restrictions.eq("importedEntity.type", prismImportedEntity));
        }

        return (List<T>) appendMappedEntityRestriction(institution, "importedEntity", criteria) //
                .addOrder(Order.asc("importedEntity.name")) //
                .list();
    }

    public List<ImportedInstitution> getEnabledImportedInstitutions(ImportedEntitySimple domicile) {
        return (List<ImportedInstitution>) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

    public List<ImportedInstitution> getEnabledImportedInstitutionsWithMappings(Institution institution, ImportedEntitySimple domicile) {
        PrismImportedEntity prismImportedEntity = IMPORTED_INSTITUTION;
        Criteria criteria = getEntityMappingSelectStatement(prismImportedEntity)
                .createAlias("importedEntity", "importedEntity", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("importedEntity.domicile", domicile)); //

        return (List<ImportedInstitution>) appendMappedEntityRestriction(institution, "importedEntity", criteria) //
                .addOrder(Order.asc("importedEntity.name")) //
                .list();
    }

    public List<ImportedProgram> getEnabledImportedPrograms(ImportedInstitution institution) {
        return (List<ImportedProgram>) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class)
                .add(Restrictions.eq("institution", institution))
                .add(Restrictions.eq("enabled", true))
                .addOrder(Order.asc("name"))
                .list();
    }

    public List<ImportedProgram> getEnabledImportedProgramsWithMappings(Institution institution, ImportedInstitution importedInstitution) {
        PrismImportedEntity prismImportedEntity = IMPORTED_PROGRAM;
        Criteria criteria = getEntityMappingSelectStatement(prismImportedEntity)
                .createAlias("importedEntity", "importedEntity", JoinType.INNER_JOIN)
                .add(Restrictions.eq("importedEntity.institution", importedInstitution));

        return (List<ImportedProgram>) appendMappedEntityRestriction(institution, "importedEntity", criteria)
                .addOrder(Order.asc("importedEntity.name"))
                .list();
    }

    public List<ImportedProgram> getImportedPrograms(String searchTerm) {
        List<String> tokens = Splitter.on(CharMatcher.WHITESPACE).omitEmptyStrings().limit(10).splitToList(searchTerm);
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class)
                .createAlias("institution", "institution", JoinType.INNER_JOIN);
        for (String token : tokens) {
            criteria.add(Restrictions.disjunction()
                    .add(Restrictions.ilike("name", token, MatchMode.ANYWHERE))
                    .add(Restrictions.ilike("institution.name", token, MatchMode.ANYWHERE)));
        }
        return criteria.setMaxResults(10).list();
    }

    public <T extends ImportedEntityMapping<?>> List<T> getImportedEntityMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        return getImportedEntityMappings(institution, prismImportedEntity, null);
    }

    public <T extends ImportedEntity<?, V>, V extends ImportedEntityMapping<T>> List<V> getEnabledImportedEntityMapping(Institution institution,
            T importedEntity) {
        return getImportedEntityMapping(institution, importedEntity, true);
    }

    public <T extends ImportedEntityMapping<?>> List<T> getEnabledImportedEntityMappings(Institution institution,
            PrismImportedEntity prismImportedEntity) {
        return getImportedEntityMappings(institution, prismImportedEntity, true);
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

    public <T extends ImportedEntityMapping<?>> void disableImportedEntityMappings(Institution institution, PrismImportedEntity prismImportedEntity) {
        String queryString = "update " + prismImportedEntity.getMappingClass().getSimpleName() + " " //
                + "set enabled = false " //
                + "where institution = :institution";

        boolean simpleEntity = prismImportedEntity.getEntityClass().equals(ImportedEntitySimple.class);
        if (simpleEntity) {
            queryString = queryString + " and importedEntity in (" //
                    + "from " + prismImportedEntity.getEntityClassUpperCamelName() + " " //
                    + "where type = :type)";
        }

        Query query = sessionFactory.getCurrentSession().createQuery(queryString)
                .setParameter("institution", institution);

        if (simpleEntity) {
            query.setParameter("type", prismImportedEntity);
        }

        query.executeUpdate();
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
        return (DomicileUseDTO) sessionFactory.getCurrentSession().createCriteria(AddressApplication.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("domicile"), "domicile") //
                        .add(Projections.count("id").as("useCount"), "useCount")) //
                .createAlias("domicile", "domicile", JoinType.INNER_JOIN) //
                .createAlias("domicile.mappings", "domicileMapping", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("domicileMapping.institution", institution)) //
                .add(Restrictions.eq("domicileMapping.enabled", true))
                .addOrder(Order.desc("useCount")) //
                .setMaxResults(1) //
                .setResultTransformer(Transformers.aliasToBean(DomicileUseDTO.class)) //
                .uniqueResult();
    }

    public ImportedAgeRange getAgeRange(Institution institution, Integer age) {
        return (ImportedAgeRange) sessionFactory.getCurrentSession().createCriteria(ImportedAgeRange.class) //
                .createAlias("mappings", "ageMapping", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("ageMapping.institution", institution)) //
                .add(Restrictions.eq("ageMapping.enabled", true))
                .add(Restrictions.ge("lowerBound", age)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.le("upperBound", age)) //
                        .add(Restrictions.isNull("upperBound"))) //
                .add(Restrictions.eq("enabled", true)) //
                .uniqueResult();
    }

    public List<ImportedInstitution> getInstitutionsWithUcasId() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class)
                .add(Restrictions.isNotNull("ucasId"))
                .addOrder(Order.asc("ucasId"))
                .list();
    }

    public List<ImportedProgramDTO> getImportedUcasPrograms() {
        return (List<ImportedProgramDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("institution.id"), "institution") //
                        .add(Projections.property("qualification"), "qualification") //
                        .add(Projections.property("name"), "name") //
                        .add(Projections.property("institution.ucasId"), "ucasId")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.isNotNull("institution.ucasId")) //
                .setResultTransformer(Transformers.aliasToBean(ImportedProgramDTO.class)) //
                .list();
    }

    public List<ImportedSubjectAreaDTO> getImportedSubjectAreas() {
        return (List<ImportedSubjectAreaDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedSubjectArea.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "name") //
                        .add(Projections.property("jacsCode"), "jacsCode") //
                        .add(Projections.property("jacsCodeOld"), "jacsCodeOld") //
                        .add(Projections.property("ucasSubject"), "ucasSubject") //
                        .add(Projections.property("parent.id"), "parent")) //
                .setResultTransformer(Transformers.aliasToBean(ImportedSubjectAreaDTO.class))
                .list();
    }
    
    public List<ImportedSubjectArea> getChildImportedSubjectAreas() {
        return (List<ImportedSubjectArea>) sessionFactory.getCurrentSession().createCriteria(ImportedSubjectArea.class) //
                .add(Restrictions.isNotNull("parent")) //
                .list();
    }

    public List<ImportedInstitutionSubjectAreaDTO> getImportedInstitutionSubjectAreas() {
        return (List<ImportedInstitutionSubjectAreaDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedProgramSubjectArea.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("program.institution.id"), "institution") //
                        .add(Projections.groupProperty("subjectArea.id"), "subjectArea") //
                        .add(Projections.sum("relationStrength"), "relationStrength")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .setResultTransformer(Transformers.aliasToBean(ImportedInstitutionSubjectAreaDTO.class)) //
                .list();
    }

    public void deleteImportedEntityTypes() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ImportedEntityType") //
                .executeUpdate();
    }
    
    public <T extends WeightedRelationImported> void disableImportedEntityRelations(Class<T> entityClass) {
        sessionFactory.getCurrentSession().createQuery( //
                "update " + entityClass.getSimpleName() + " " //
                        + "set enabled = false") //
                .executeUpdate();
    }
    
    public void executeBulkMerge(String table, String columns, String inserts, String updates) {
        sessionFactory.getCurrentSession().createSQLQuery(
                "insert into " + table + " (" + columns + ") "
                        + "values " + inserts + " "
                        + "on duplicate key update " + updates)
                .executeUpdate();
    }
    
    private <T extends ImportedEntity<?, V>, V extends ImportedEntityMapping<T>> List<V> getImportedEntityMapping(Institution institution, T importedEntity,
            Boolean enabled) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(importedEntity.getType().getMappingClass()) //
                .createAlias("importedEntity", "importedEntity", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("importedEntity", importedEntity)) //
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
        Class<T> mappingClass = (Class<T>) importedEntity.getMappingClass();

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappingClass) //
                .createAlias("importedEntity", "importedEntity", JoinType.INNER_JOIN);

        if (ImportedEntitySimpleMapping.class.isAssignableFrom(mappingClass)) {
            criteria.add(Restrictions.eq("importedEntity.type", importedEntity));
        }

        criteria.add(Restrictions.eq("institution", institution));

        if (enabled != null) {
            criteria.add(Restrictions.eq("enabled", enabled));
        }

        return (List<T>) criteria.addOrder(Order.desc("importedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    private Criteria getEntityMappingSelectStatement(PrismImportedEntity prismImportedEntity) {
        return sessionFactory.getCurrentSession().createCriteria(prismImportedEntity.getMappingClass()) //
                .setProjection(Projections.groupProperty("importedEntity"));
    }

    private Criteria appendMappedEntityRestriction(Institution institution, String entityReference, Criteria criteria) {
        return criteria.add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.isNotNull("code")) //
                .add(Restrictions.eq("enabled", true)); //
    }

}
