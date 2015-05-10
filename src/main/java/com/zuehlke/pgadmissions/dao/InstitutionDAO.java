package com.zuehlke.pgadmissions.dao;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntityFeed;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.resource.ResourceState;
import com.zuehlke.pgadmissions.dto.ResourceForWhichUserCanCreateChildDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import freemarker.template.Template;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.INSTITUTION_APPROVED;

@Repository
@SuppressWarnings("unchecked")
public class InstitutionDAO {

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private FreeMarkerConfig freemarkerConfig;

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

    public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> programStates, List<PrismState> projectStates) {
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
                .add(Restrictions.not( //
                        Restrictions.conjunction() //
                                .add(Restrictions.isNull("program.id")) //
                                .add(Restrictions.isNull("project.id")))) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
                .list();
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer institutionId, List<PrismState> institutionStates, List<PrismState> programStates,
                                                       List<PrismState> projectStates) {
        return (SearchEngineAdvertDTO) sessionFactory.getCurrentSession().createCriteria(Institution.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution.id"), "institutionId") //
                        .add(Projections.property("institution.title"), "institutionTitle") //
                        .add(Projections.property("institution.summary"), "institutionSummary") //
                        .add(Projections.property("institution.homepage"), "institutionHomepage")) //
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

    public List<InstitutionDomicile> getInstitutionDomiciles() {
        return sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class) //
                .add(Restrictions.eq("enabled", true)) //
                .addOrder(Order.asc("name")) //
                .list();
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

    public List<Institution> getInstitutions(String query, String[] googleIds) {
        return sessionFactory.getCurrentSession().createCriteria(Institution.class)
                .createAlias("advert", "advert", JoinType.LEFT_OUTER_JOIN)
                .createAlias("advert.address", "address", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.disjunction()
                        .add(Restrictions.ilike("title", query, MatchMode.ANYWHERE))
                        .add(Restrictions.in("address.googleId", googleIds)))
                .add(Restrictions.eq("state.id", PrismState.INSTITUTION_APPROVED_COMPLETED))
                .list();
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProgram(List<PrismState> states, boolean userLoggedIn) {
        Junction disjunction = Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("institution.id", "userRole.institution.id")) //
                                .add(Restrictions.eqProperty("institution.system.id", "userRole.system.id"))) //
                        .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_PROGRAM)) //
                        .add(Restrictions.eq("resourceCondition.partnerMode", false)) //
                        .add(Restrictions.eq("action.creationScope.id", PROGRAM))) //
                .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_PROGRAM));

        if (!userLoggedIn) {
            disjunction.add(Restrictions.eq("resourceCondition.partnerMode", true));
        }

        return (List<ResourceForWhichUserCanCreateChildDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(disjunction) //
                .addOrder(Order.asc("institution.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceForWhichUserCanCreateChildDTO.class)) //
                .list();
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsForWhichUserCanCreateProject(List<PrismState> states, boolean userLoggedIn) {
        Junction disjunction = Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("institution.id", "userRole.institution.id")) //
                                .add(Restrictions.eqProperty("institution.system.id", "userRole.system.id"))) //
                        .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_PROJECT)) //
                        .add(Restrictions.eq("resourceCondition.partnerMode", false)) //
                        .add(Restrictions.eq("action.creationScope.id", PROJECT))) //
                .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_PROJECT));

        if (!userLoggedIn) {
            disjunction.add(Restrictions.eq("resourceCondition.partnerMode", true));
        }

        return (List<ResourceForWhichUserCanCreateChildDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(disjunction) //
                .addOrder(Order.asc("institution.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceForWhichUserCanCreateChildDTO.class)) //
                .list();
    }

    public List<ResourceForWhichUserCanCreateChildDTO> getInstitutionsWhichHaveProgramsForWhichUserCanCreateProject(List<PrismState> states,
                                                                                                                    boolean userLoggedIn) {
        Junction disjunction = Restrictions.disjunction() //
                .add(Restrictions.conjunction() //
                        .add(Restrictions.disjunction() //
                                .add(Restrictions.eqProperty("program.id", "userRole.program.id"))
                                .add(Restrictions.eqProperty("program.institution.id", "userRole.institution.id")) //
                                .add(Restrictions.eqProperty("program.system.id", "userRole.system.id"))) //
                        .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_PROJECT)) //
                        .add(Restrictions.eq("resourceCondition.partnerMode", false)) //
                        .add(Restrictions.eq("action.creationScope.id", PROJECT))) //
                .add(Restrictions.eq("resourceCondition.actionCondition", ACCEPT_PROJECT));

        if (!userLoggedIn) {
            disjunction.add(Restrictions.eq("resourceCondition.partnerMode", true));
        }

        return (List<ResourceForWhichUserCanCreateChildDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("institution"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.programs", "program", JoinType.INNER_JOIN) //
                .createAlias("program.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateAction.stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .add(Restrictions.in("state.id", states)) //
                .add(disjunction) //
                .addOrder(Order.asc("institution.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceForWhichUserCanCreateChildDTO.class)) //
                .list();
    }

}
