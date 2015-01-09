package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction.PROJECT_CREATE_APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_APPROVED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DEACTIVATED;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_PENDING_REACTIVATION;

import java.util.Arrays;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.workflow.State;
import com.zuehlke.pgadmissions.dto.AdvertSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.ResourceSearchEngineDTO;
import com.zuehlke.pgadmissions.dto.SitemapEntryDTO;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void synchronizeProjectEndDates(Program program) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Project " //
                        + "set endDate = :baseline " //
                        + "where program = :program " //
                        + "and endDate < :baseline") //
                .setParameter("program", program) //
                .setParameter("baseline", program.getEndDate()) //
                .executeUpdate();
    }

    public void synchronizeProjectDueDates(Program program) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Project " //
                        + "set dueDate = :baseline " //
                        + "where program = :program " //
                        + "and dueDate < :baseline") //
                .setParameter("program", program) //
                .setParameter("baseline", program.getDueDate()) //
                .executeUpdate();
    }

    public State getPreviousState(Project project) {
        return (State) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("state")) //
                .add(Restrictions.eq("project", project)) // /
                .add(Restrictions.isNotNull("state")) //
                .add(Restrictions.ne("state", project.getState())) //
                .add(Restrictions.in("state.id", Arrays.asList(PROJECT_APPROVED, PROJECT_DEACTIVATED))) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Long getActiveProjectCount(ResourceParent resource) {
        String resourceReference = resource.getResourceScope().getLowerCaseName();
        return (Long) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.countDistinct("id")) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias("resourceStates", "resourceState", JoinType.INNER_JOIN) //
                .createAlias("resourceState.state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateActions", "stateAction", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.eq("stateAction.action.id", PROJECT_CREATE_APPLICATION)) //
                .uniqueResult();
    }

    public List<Project> getProjectsPendingReactivation(Program program, LocalDate baseline) {
        return (List<Project>) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .add(Restrictions.eq("program", program)) //
                .add(Restrictions.eq("state.id", PROJECT_DISABLED_PENDING_REACTIVATION)) //
                .add(Restrictions.ge("endDate", baseline)) //
                .list();
    }

    public DateTime getLatestUpdatedTimestampSitemap(List<PrismState> states) {
        return (DateTime) sessionFactory.getCurrentSession().createCriteria(Project.class) //
                .setProjection(Projections.property("updatedTimestampSitemap")) //
                .add(Restrictions.in("state.id", states)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<SitemapEntryDTO> getSitemapEntries(List<PrismState> states) {
        return (List<SitemapEntryDTO>) sessionFactory.getCurrentSession().createCriteria(Project.class, "project") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "resourceId") //
                        .add(Projections.property("updatedTimestampSitemap"), "lastModifiedTimestamp")) //
                .add(Restrictions.in("state.id", states)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setMaxResults(50000) //
                .setResultTransformer(Transformers.aliasToBean(SitemapEntryDTO.class)) //
                .list();
    }

    public AdvertSearchEngineDTO getSearchEngineAdvert(Integer projectId, List<PrismState> states) {
        return (AdvertSearchEngineDTO) sessionFactory.getCurrentSession().createCriteria(Project.class, "project") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("advert.title"), "projectTitle") //
                        .add(Projections.property("advert.summary"), "projectSummary") //
                        .add(Projections.property("advert.description"), "projectDescription") //
                        .add(Projections.property("programAdvert.title"), "programTitle") //
                        .add(Projections.property("programAdvert.summary"), "programSummary") //
                        .add(Projections.property("programAdvert.description"), "programDescription") //
                        .add(Projections.property("institution.title"), "institutionTitle") //
                        .add(Projections.property("institution.summary"), "institutionSummary") //
                        .add(Projections.property("institution.homepage"), "institutionHomepage")) //
                .createAlias("advert", "advert", JoinType.INNER_JOIN) //
                .createAlias("program", "program", JoinType.INNER_JOIN) //
                .createAlias("program.advert", "programAdvert", JoinType.INNER_JOIN) //
                .createAlias("institution", "institution", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("id", projectId)) //
                .add(Restrictions.in("state.id", states)) //
                .setResultTransformer(Transformers.aliasToBean(AdvertSearchEngineDTO.class)) //
                .uniqueResult();
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByProgram(Integer programId, List<PrismState> states) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Project.class, "project") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("program.id", programId)) //
                .add(Restrictions.in("state.id", states)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
                .list();
    }

    public List<ResourceSearchEngineDTO> getActiveProjectsByInstitution(Integer institutionId, List<PrismState> states) {
        return (List<ResourceSearchEngineDTO>) sessionFactory.getCurrentSession().createCriteria(Project.class, "project") //
                .setProjection(Projections.projectionList() //
                        .add(Projections.property("id"), "id") //
                        .add(Projections.property("title"), "title")) //
                .add(Restrictions.eq("institution.id", institutionId)) //
                .add(Restrictions.in("state.id", states)) //
                .addOrder(Order.desc("updatedTimestampSitemap")) //
                .setResultTransformer(Transformers.aliasToBean(ResourceSearchEngineDTO.class)) //
                .list();
    }

}
