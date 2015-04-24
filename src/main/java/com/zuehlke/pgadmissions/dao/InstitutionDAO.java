package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionSuggestionDTO;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Institution> getApprovedInstitutionsByCountry(InstitutionDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("domicile", domicile)) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED_COMPLETED)) //
                .addOrder(Order.asc("title")) //
                .list();
    }

    public Institution getUclInstitution() {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("uclInstitution", true)) //
                .uniqueResult();
    }

    public List<Institution> getInstitutionsWithoutImportedEntityFeeds() {
        return (List<Institution>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.isEmpty("importedEntityFeeds")) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED)) //
                .list();
    }

    public List<String> listAvailableCurrencies() {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class) //
                .setProjection(Projections.distinct(Projections.property("currency"))) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("currency")) //
                .list();
    }

    public List<InstitutionSuggestionDTO> getSimilarImportedInsitutions(Integer domicileId, String searchTerm) {
        return (List<InstitutionSuggestionDTO>) sessionFactory.getCurrentSession().createCriteria(ImportedInstitution.class, "institution") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("name"), "title")) //
                .add(Restrictions.eq("domicile.id", "domicileId")) //
                .add(Restrictions.eq("domicile.enabled", true)) //
                .add(Restrictions.eq("enabled", true)) //
                .add(Restrictions.ilike("name", "searchTerm", MatchMode.ANYWHERE)) //
                .addOrder(Order.asc("name")) //
                .setMaxResults(10) //
                .setResultTransformer(Transformers.aliasToBean(InstitutionSuggestionDTO.class)) //
                .list();
    }

    public List<Integer> getInstitutionsToActivate() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .add(Restrictions.isNotEmpty("importedEntityFeeds")) //
                .list();
    }

    public List<Institution> list() {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class).list();
    }

    public Long getAuthenticatedFeedCount(Institution institution) {
        return (Long) sessionFactory.getCurrentSession().createCriteria(ImportedEntityFeed.class) //
                .setProjection(Projections.rowCount()) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.isNotNull("username")).uniqueResult();
    }

    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("googleId", googleId)) //
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED_COMPLETED)) //
                .uniqueResult();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> programStates, List<PrismState> projectStates) {
        return (DateTime) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.property("updatedTimestampSitemap")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.in("program.state.id", programStates)) //
                .add(Restrictions.in("project.state.id", projectStates)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNotNull("program.id")) //
                        .add(Restrictions.isNotNull("project.id"))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> programStates, List<PrismState> projectStates) {
        return (List<SitemapEntryDTO>) sessionFactory.getCurrentSession().createCriteria(Institution.class, "institution") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "resourceId") //
                        .add(Projections.property("updatedTimestampSitemap"), "lastModifiedTimestamp")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, Restrictions.in("program.state.id", programStates)) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, Restrictions.in("project.state.id", projectStates)) //
                .add(Restrictions.not( //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNull("program.id")) //
                                .add(Restrictions.isNull("project.id")))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
                .list();
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer institutionId, List<PrismState> programStates, List<PrismState> projectStates) {
        return (SearchEngineAdvertDTO) sessionFactory.getCurrentSession().createCriteria(Institution.class, "institution") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution.id"), "institutionId") //
                        .add(Projections.property("institution.title"), "institutionTitle") //
                        .add(Projections.property("institution.summary"), "institutionSummary") //
                        .add(Projections.property("institution.homepage"), "institutionHomepage")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, Restrictions.in("program.state.id", programStates)) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, Restrictions.in("project.state.id", projectStates)) //
                .add(Restrictions.not( //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNull("program.id")) //
                                .add(Restrictions.isNull("project.id")))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .add(Restrictions.eq("id", institutionId)) //
                .setResultTransformer(Transformers.aliasToBean(SearchEngineAdvertDTO.class)) //
                .uniqueResult();
    }

    public List<ResourceSearchEngineDTO> getRelatedInstitutions(List<PrismState> programStates, List<PrismState> projectStates) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Institution.class, "institution") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, Restrictions.in("program.state.id", programStates)) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, Restrictions.in("project.state.id", projectStates)) //
                .add(Restrictions.not( //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNull("program.id")) //
                                .add(Restrictions.isNull("project.id")))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
                .list();
    }

    public void disableInstitutionDomiciles(List<String> updates) {
        sessionFactory.getCurrentSession().createQuery( //
                "update InstitutionDomicile " //
                        + "set enabled = false " //
                        + "where id not in (:updates)") //
                .setParameterList("updates", updates) //
                .executeUpdate();
    }

    public List<InstitutionDomicile> getInstitutionDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
    }

}
