package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceConditionConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.dto.ResourceForWhichUserCanCreateChildDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;

import freemarker.template.Template;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

    public List<Institution> getApprovedInstitutionsByDomicile(ImportedAdvertDomicile domicile) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("advert.domicile", domicile)) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .addOrder(Order.asc("title")) //
                .list();
    }

    public Institution getUclInstitution() {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("uclInstitution", true)) //
                .uniqueResult();
    }

    public List<String> listAvailableCurrencies() {
        return sessionFactory.getCurrentSession().createCriteria(ImportedAdvertDomicile.class) //
                .setProjection(Projections.distinct(Projections.property("currency"))) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("currency")) //
                .list();
    }

    public List<Institution> list() {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class).list();
    }

    public List<Integer> getApprovedInstitutions() {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.property("id")) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resourceState.state.id", INSTITUTION_APPROVED)) //
                .list();
    }
    
    public Institution getActivatedInstitutionByGoogleId(String googleId) {
        return (Institution) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .add(Restrictions.eq("googleId", googleId)) //
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED)) //
                .uniqueResult();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> programStates, List<PrismState> projectStates) {
        return (DateTime) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.property("updatedTimestampSitemap")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.isNotEmpty("program.resourceStates")) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.isNotEmpty("project.resourceStates")) //
                .add(Restrictions.in("program.state.id", programStates)) //
                .add(Restrictions.in("project.state.id", projectStates)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNotNull("program.id")) //
                        .add(Restrictions.isNotNull("project.id"))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> institutionStates, List<PrismState> programStates, List<PrismState> projectStates) {
        return (List<SitemapEntryDTO>) sessionFactory.getCurrentSession().createCriteria(Institution.class, "institution") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "resourceId") //
                        .add(Projections.property("updatedTimestampSitemap"), "lastModifiedTimestamp")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("program.state.id", programStates)) //
                                .add(Restrictions.isNotEmpty("program.resourceConditions"))) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("project.state.id", projectStates)) //
                                .add(Restrictions.isNotEmpty("project.resourceConditions"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.in("state.id", institutionStates)) //
                                .add(Restrictions.isNotEmpty("resourceConditions"))) //
                        .add(Restrictions.not( //
                                Restrictions.conjunction() //
                                        .add(Restrictions.isNull("program.id")) //
                                        .add(Restrictions.isNull("project.id"))))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
                .list();
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer institutionId, List<PrismState> institutionStates, List<PrismState> programStates,
            List<PrismState> projectStates) {
        return (SearchEngineAdvertDTO) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "institutionId") //
                        .add(Projections.property("advert.title"), "institutionTitle") //
                        .add(Projections.property("advert.summary"), "institutionSummary") //
                        .add(Projections.property("advert.homepage"), "institutionHomepage")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("program.state.id", programStates)) //
                                .add(Restrictions.isNotEmpty("program.resourceConditions"))) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("project.state.id", projectStates)) //
                                .add(Restrictions.isNotEmpty("project.resourceConditions"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.in("state.id", institutionStates)) //
                                .add(Restrictions.isNotEmpty("resourceConditions"))) //
                        .add(Restrictions.not( //
                                Restrictions.conjunction() //
                                        .add(Restrictions.isNull("program.id")) //
                                        .add(Restrictions.isNull("project.id"))))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .add(Restrictions.eq("id", institutionId)) //
                .setResultTransformer(Transformers.aliasToBean(SearchEngineAdvertDTO.class)) //
                .uniqueResult();
    }

    public List<ResourceSearchEngineDTO> getRelatedInstitutions(List<PrismState> institutionStates, List<PrismState> programStates,
            List<PrismState> projectStates) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .createAlias("programs", "program", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("program.state.id", programStates)) //
                                .add(Restrictions.isNotEmpty("program.resourceConditions"))) //
                .createAlias("projects", "project", JoinType.LEFT_OUTER_JOIN, //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("project.state.id", projectStates)) //
                                .add(Restrictions.isNotEmpty("project.resourceConditions"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.in("state.id", institutionStates)) //
                                .add(Restrictions.isNotEmpty("resourceConditions"))) //
                        .add(Restrictions.not( //
                                Restrictions.conjunction() //
                                        .add(Restrictions.isNull("program.id")) //
                                        .add(Restrictions.isNull("project.id"))))) //
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
    
    public void changeInstitutionBusinessYear(Integer institutionId, Integer businessYearEndMonth) throws Exception {
        String templateLocation;

        Map<String, Object> model = Maps.newHashMap();
        model.put("institutionId", institutionId);

        if (businessYearEndMonth == 12) {
            templateLocation = "sql/institution_change_business_year_simple.ftl";
        } else {
            templateLocation = "sql/institution_change_business_year_complex.ftl";
            model.put("businessYearEndMonth", businessYearEndMonth);
        }

        String statement = Resources.toString(Resources.getResource(templateLocation), Charsets.UTF_8);
        Template template = new Template("statement", statement, freemarkerConfig.getConfiguration());

        sessionFactory.getCurrentSession().createSQLQuery( //
                FreeMarkerTemplateUtils.processTemplateIntoString(template, model)) //
                .executeUpdate();
    }

    public List<Institution> getInstitutions(String searchTerm, String[] googleIds) {
        Criterion searchCriterion = Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE);
        if (googleIds != null && googleIds.length > 0) {
            searchCriterion = Restrictions.disjunction()
                    .add(searchCriterion)
                    .add(Restrictions.in("googleId", googleIds));
        }

        return sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .add(searchCriterion)
                .add(Restrictions.eq("state.id", INSTITUTION_APPROVED))
                .list();
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProgram(List<PrismState> states, boolean userLoggedIn) {
        return (List<ResourceForWhichUserCanCreateChildDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROGRAM, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROGRAM)) //
                .addOrder(Order.asc("institution.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceForWhichUserCanCreateChildDTO.class)) //
                .list();
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProject(List<PrismState> states, boolean userLoggedIn) {
        return (List<ResourceForWhichUserCanCreateChildDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROJECT, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROJECT)) //
                .addOrder(Order.asc("institution.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceForWhichUserCanCreateChildDTO.class)) //
                .list();
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(List<PrismState> states,
            boolean userLoggedIn) {
        return (List<ResourceForWhichUserCanCreateChildDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("program.institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("program.institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("program.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROJECT, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROJECT)) //
                .addOrder(Order.asc("institution.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceForWhichUserCanCreateChildDTO.class)) //
                .list();
    }

}
