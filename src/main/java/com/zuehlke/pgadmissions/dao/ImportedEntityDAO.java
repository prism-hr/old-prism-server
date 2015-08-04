package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity.IMPORTED_PROGRAM;

import java.math.BigDecimal;
import java.util.Collection;
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
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitutionSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgram;
import com.zuehlke.pgadmissions.domain.imported.ImportedProgramSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.ImportedSubjectArea;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntityMapping;
import com.zuehlke.pgadmissions.domain.imported.mapping.ImportedEntitySimpleMapping;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.DomicileUseDTO;
import com.zuehlke.pgadmissions.dto.ImportedInstitutionSubjectAreaDTO;

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

    public <T extends ImportedEntity<?, ?>> List<T> getSimilarImportedEntities(Class<T> entityClass, String searchTerm) {
        return (List<T>) sessionFactory.getCurrentSession().createCriteria(entityClass)
                .add(Restrictions.ilike("name", searchTerm, MatchMode.ANYWHERE))
                .add(Restrictions.eq("enabled", true))
                .list();
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

    public List<ImportedInstitution> getImportedUcasInstitutions() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class)
                .add(Restrictions.isNotNull("ucasId"))
                .addOrder(Order.asc("ucasId"))
                .list();
    }

    public List<Integer> getRootImportedSubjectAreas() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ImportedSubjectArea.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.isNull("parent")) //
                .list();
    }

    public List<Integer> getChildImportedSubjectAreas(Integer... subjectAreas) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(ImportedSubjectArea.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.in("parent.id", subjectAreas)) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public Long getUnindexedProgramCount() {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .setProjection(Projections.count("id")) //
                .add(Restrictions.eq("indexed", false)) //
                .uniqueResult();
    }

    public Long getUnindexedUcasProgramCount() {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .setProjection(Projections.count("id")) //
                .add(Restrictions.isNotNull("ucasCode")) //
                .add(Restrictions.eq("indexed", false)) //
                .uniqueResult();
    }

    public Long getUnindexedInstitutionCount() {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class) //
                .setProjection(Projections.count("id")) //
                .add(Restrictions.eq("indexed", false)) //
                .uniqueResult();
    }

    public List<ImportedProgram> getUnindexedImportedUcasPrograms() {
        return (List<ImportedProgram>) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .add(Restrictions.isNotNull("ucasCode")) //
                .add(Restrictions.eq("indexed", false)) //
                .list();
    }

    public Double getAverageImportedProgramUcasProgramCount() {
        return (Double) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .setProjection(Projections.avg("ucasProgramCount"))
                .add(Restrictions.isNotNull("ucasCode")) //
                .uniqueResult();
    }

    public void fillImportedProgramUcasProgramCount(Integer ucasProgramCount) {
        sessionFactory.getCurrentSession().createQuery( //
                "update ImportedProgram " //
                        + "set ucasProgramCount = :ucasProgramCount "
                        + "where ucasProgramCount is null") //
                .setParameter("ucasProgramCount", ucasProgramCount) //
                .executeUpdate();
    }

    public List<ImportedProgram> getUnindexedImportedNonUcasPrograms() {
        return (List<ImportedProgram>) sessionFactory.getCurrentSession().createCriteria(ImportedProgram.class) //
                .add(Restrictions.isNull("ucasCode")) //
                .add(Restrictions.isNotNull("ucasProgramCount")) //
                .add(Restrictions.eq("indexed", false)) //
                .list();
    }

    public List<ImportedInstitutionSubjectAreaDTO> getImportedInstitutionSubjectAreas(ImportedInstitution importedInstitution) {
        return (List<ImportedInstitutionSubjectAreaDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedProgramSubjectArea.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("subjectArea.id"), "id") //
                        .add(Projections.groupProperty("relationStrength"), "relationStrength") //
                        .add(Projections.count("id"), "relationCount")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program.institution", importedInstitution)) //
                .addOrder(Order.asc("subjectArea.id")) //
                .addOrder(Order.desc("relationStrength")) //
                .setResultTransformer(Transformers.aliasToBean(ImportedInstitutionSubjectAreaDTO.class)) //
                .list();
    }

    public void deleteImportedEntityTypes() {
        sessionFactory.getCurrentSession().createQuery( //
                "delete ImportedEntityType") //
                .executeUpdate();
    }

    public BigDecimal getMinimumImportedInstitutionSubjectAreaRelationStrength(Integer concentrationFactor, BigDecimal proliferationFactor,
            Collection<Integer> institutions, Integer... subjectAreas) {
        return (BigDecimal) sessionFactory.getCurrentSession().createCriteria(ImportedInstitutionSubjectArea.class) //
                .setProjection(Projections.property("relationStrength")) //
                .add(Restrictions.in("institution.id", institutions)) //
                .add(Restrictions.in("subjectArea.id", subjectAreas)) //
                .add(Restrictions.eq("concentrationFactor", concentrationFactor)) //
                .add(Restrictions.eq("proliferationFactor", proliferationFactor)) //
                .addOrder(Order.asc("relationStrength")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<ImportedInstitutionSubjectAreaDTO> getImportedInstitutionSubjectAreas(Integer concentrationFactor, BigDecimal proliferationFactor,
            BigDecimal minimumRelationStrength, Integer... subjectAreas) {
        return (List<ImportedInstitutionSubjectAreaDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedInstitutionSubjectArea.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution.id"), "id") //
                        .add(Projections.sum("relationStrength").as("cumulativeRelationStrength"), "relationStrength")) //
                .add(Restrictions.in("subjectArea.id", subjectAreas)) //
                .add(Restrictions.eq("concentrationFactor", concentrationFactor)) //
                .add(Restrictions.eq("proliferationFactor", proliferationFactor)) //
                .add(Restrictions.ge("relationStrength", minimumRelationStrength)) //
                .addOrder(Order.desc("cumulativeRelationStrength")) //
                .addOrder(Order.asc("institution.id")) //
                .setResultTransformer(Transformers.aliasToBean(ImportedInstitutionSubjectAreaDTO.class)) //
                .list();
    }

    public void enableImportedInstitutionSubjectAreas(Integer concentrationFactor, BigDecimal proliferationFactor, Integer... subjectAreas) {
        sessionFactory.getCurrentSession().createQuery(
                "update ImportedInstitutionSubjectArea "
                        + "set enabled = true "
                        + "where subjectArea.id in (:subjectAreas) "
                        + "and concentrationFactor = :concentrationFactor "
                        + "and proliferationFactor = :proliferationFactor") //
                .setParameterList("subjectAreas", subjectAreas) //
                .setParameter("concentrationFactor", concentrationFactor) //
                .setParameter("proliferationFactor", proliferationFactor) //
                .executeUpdate();
    }

    public void deleteImportedInstitutionSubjectAreas(Integer... subjectAreas) {
        sessionFactory.getCurrentSession().createQuery(
                "delete ImportedInstitutionSubjectArea "
                        + "where subjectArea.id in (:subjectAreas) "
                        + "and enabled is true") //
                .setParameterList("subjectAreas", subjectAreas) //
                .executeUpdate();
    }

    public void deleteImportedInstitutionSubjectAreas(Integer concentrationFactor, BigDecimal proliferationFactor, Integer... subjectAreas) {
        sessionFactory.getCurrentSession().createQuery(
                "delete ImportedInstitutionSubjectArea "
                        + "where subjectArea.id in (:subjectAreas) "
                        + "and concentrationFactor = :concentrationFactor "
                        + "and proliferationFactor = :proliferationFactor") //
                .setParameterList("subjectAreas", subjectAreas) //
                .setParameter("concentrationFactor", concentrationFactor) //
                .setParameter("proliferationFactor", proliferationFactor) //
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
