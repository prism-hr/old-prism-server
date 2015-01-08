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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.workflow.State;

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

}
