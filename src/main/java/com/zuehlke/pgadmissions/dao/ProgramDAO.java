package com.zuehlke.pgadmissions.dao;

import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.resource.*;
import com.zuehlke.pgadmissions.dto.ResourceChildCreationDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import org.hibernate.FetchMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static com.zuehlke.pgadmissions.dao.WorkflowDAOUtils.getResourceConditionConstraint;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCondition.ACCEPT_PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.*;

@Repository
@SuppressWarnings("unchecked")
public class ProgramDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Program getProgramByCode(String code) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("studyOptions", FetchMode.JOIN) //
                .add(Restrictions.eq("code", code)) //
                .uniqueResult();
    }

    public Program getProgramByImportedCode(Institution institution, String importedCode) {
        return (Program) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setFetchMode("studyOptions", FetchMode.JOIN) //
                .add(Restrictions.eq("institution", institution)) //
                .add(Restrictions.eq("importedCode", importedCode)) //
                .uniqueResult();
    }

    public List<ResourceRepresentationSimple> getApprovedPrograms(Integer institutionId) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id")) //
                        .add(Projections.property("title"))) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.eq("resourceState.state.id", PROGRAM_APPROVED)) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class)) //
                .list();
    }

    public List<ResourceRepresentationSimple> getSimilarPrograms(Integer institutionId, String searchTerm) {
        return (List<ResourceRepresentationSimple>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.not( //
                        Restrictions.in("state.id", Arrays.asList(PROGRAM_REJECTED, PROGRAM_WITHDRAWN, PROGRAM_DISABLED_COMPLETED)))) //
                .add(Restrictions.ilike("title", searchTerm, MatchMode.ANYWHERE)) //
                .addOrder(Order.desc("title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceRepresentationSimple.class)) //
                .list();
    }

    public List<String> getSuggestedDivisions(Program program, String location) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("studyDetail.studyDivision")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyDetail.studyLocation", location)) //
                .add(Subqueries.in(location, DetachedCriteria.forClass(ResourceStudyLocation.class) //
                        .setProjection(Projections.property("studyLocation")) //
                        .add(Restrictions.eq("program", program)))) //
                .list();
    }

    public List<String> getSuggestedStudyAreas(Program program, String location, String division) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.groupProperty("studyDetail.studyArea")) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("studyDetail.studyLocation", location)) //
                .add(Restrictions.eq("studyDetail.studyDivision", division)) //
                .add(Subqueries.in(location, DetachedCriteria.forClass(ResourceStudyLocation.class) //
                        .setProjection(Projections.property("studyLocation")) //
                        .add(Restrictions.eq("program", program)))) //
                .list();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return (DateTime) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.property("updatedTimestampSitemap")) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> states) {
        return (List<SitemapEntryDTO>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "resourceId") //
                        .add(Projections.property("updatedTimestampSitemap"), "lastModifiedTimestamp")) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
                .list();
    }

    public SearchEngineAdvertDTO getSearchEngineAdvert(Integer programId, List<PrismState> states) {
        return (SearchEngineAdvertDTO) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "programId") //
                        .add(Projections.property("advert.title"), "programTitle") //
                        .add(Projections.property("advert.summary"), "programSummary") //
                        .add(Projections.property("advert.description"), "programDescription") //
                        .add(Projections.property("institution.id"), "institutionId") //
                        .add(Projections.property("institutionAdvert.title"), "institutionTitle") //
                        .add(Projections.property("institutionAdvert.summary"), "institutionSummary") //
                        .add(Projections.property("institutionAdvert.homepage"), "institutionHomepage")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .createAlias("institution.advert", "institutionAdvert", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", programId)) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .setResultTransformer(Transformers.aliasToBean(SearchEngineAdvertDTO.class)) //
                .uniqueResult();
    }

    public List<ResourceSearchEngineDTO> getActiveProgramsByInstitution(Integer institutionId, List<PrismState> states) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Program.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.in("state.id", states)) //
                .add(Restrictions.isNotEmpty("resourceConditions")) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
                .list();
    }

    public List<Integer> getProjects(Integer program) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("program.id", program)) //
                .list();
    }

    public List<Integer> getApplications(Integer program) {
        return (List<Integer>) sessionFactory.getCurrentSession().createCriteria(Application.class) //
                .setProjection(Projections.property("id")) //
                .add(Restrictions.eq("program.id", program)) //
                .list();
    }

    public List<ResourceChildCreationDTO> getProgramsForWhichUserCanCreateProject(Integer institutionId, List<PrismState> states,
            boolean userLoggedIn) {
        return (List<ResourceChildCreationDTO>) sessionFactory.getCurrentSession().createCriteria(ResourceState.class) //
                .setProjection(Projections.projectionList() //
                        .add(Projections.groupProperty("program"), "resource") //
                        .add(Projections.max("resourceCondition.partnerMode"), "partnerMode")) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("program.resourceConditions", "resourceCondition", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .createAlias("stateAction.action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("program.institution.id", institutionId)) //
                .add(Restrictions.in("state.id", states)) //
                .add(getResourceConditionConstraint(ACCEPT_PROJECT, userLoggedIn)) //
                .add(Restrictions.eq("action.creationScope.id", PROJECT))
                .addOrder(Order.asc("program.title")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceChildCreationDTO.class)) //
                .list();
    }

}
