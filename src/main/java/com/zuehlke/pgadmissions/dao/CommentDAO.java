package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.ESCALATE_RESOURCE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismActionCategory.IMPORT_RESOURCE;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentTimeslot;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismStateGroup;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Comment getLatestComment(Resource resource) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("action.id", actionId)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, PrismAction actionId, User user, DateTime baseline) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("action.id", actionId)) //
                .add(Restrictions.eq("user", user)) //
                .add(Restrictions.ge("createdTimestamp", baseline)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public <T extends Resource> Comment getEarliestComment(ResourceParent parentResource, Class<T> resourceClass, PrismAction actionId) {
        String resourceReference = PrismScope.getResourceScope(resourceClass).getLowerCaseName();
        String parentResourceReference = parentResource.getResourceScope().getLowerCaseName();
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .createAlias(resourceReference + "." + parentResourceReference, parentResourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.id", actionId)) //
                .add(Restrictions.eq(parentResourceReference + ".id", parentResource.getId())) //
                .addOrder(Order.asc("createdTimestamp")) //
                .addOrder(Order.asc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Comment> getApplicationAssessmentComments(Application application) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.eq("applicationInterested", true)) //
                .addOrder(Order.asc("user")) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public List<User> getAppointmentInvitees(Comment comment) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", Lists.newArrayList(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE, //
                        PrismRole.APPLICATION_POTENTIAL_INTERVIEWER, PrismRole.APPLICATION_INTERVIEWEE, PrismRole.APPLICATION_INTERVIEWER))) //
                .add(Restrictions.in("roleTransitionType", Arrays.asList(PrismRoleTransitionType.CREATE, PrismRoleTransitionType.BRANCH))) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }

    public List<CommentAppointmentTimeslot> getAppointmentTimeslots(Comment schedulingComment) {
        return (List<CommentAppointmentTimeslot>) sessionFactory.getCurrentSession().createCriteria(CommentAppointmentTimeslot.class) //
                .add(Restrictions.eq("comment", schedulingComment)) //
                .addOrder(Order.asc("dateTime")) //
                .list();
    }

    public List<LocalDateTime> getAppointmentPreferences(Comment preferenceComment) {
        return (List<LocalDateTime>) sessionFactory.getCurrentSession().createCriteria(CommentAppointmentPreference.class) //
                .setProjection(Projections.property("dateTime")) //
                .add(Restrictions.eq("comment", preferenceComment)) //
                .addOrder(Order.asc("dateTime")) //
                .list();
    }

    public List<CommentAssignedUser> getAssignedSupervisors(Comment comment) {
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", Lists.newArrayList(PrismRole.APPLICATION_PRIMARY_SUPERVISOR, //
                        PrismRole.APPLICATION_SECONDARY_SUPERVISOR))) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }

    public List<String> getDeclinedSupervisors(Comment assignmentComment) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("user.email")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.ge("createdTimestamp", assignmentComment.getCreatedTimestamp())) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_ASSIGN_SUPERVISORS)) //
                .add(Restrictions.eq("recruiterAcceptAppointment", false)) //
                .list();
    }

    public List<User> getAssignedUsers(Comment comment, PrismRole roleId) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.eq("role.id", roleId)) //
                .list();
    }

    public <T extends Resource> List<Comment> getRecentComments(Class<T> resourceClass, Integer resourceId, DateTime rangeStart, DateTime rangeClose) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resourceClass).getLowerCaseName() + ".id", resourceId)) //
                .add(Restrictions.between("createdTimestamp", rangeStart, rangeClose)) //
                .list();
    }

    public List<Comment> getStateGroupTransitionComments(Resource resource) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.INNER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.INNER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .add(Restrictions.not( //
                        Restrictions.conjunction() //
                                .add(Restrictions.in("action.actionCategory", Arrays.asList(ESCALATE_RESOURCE, IMPORT_RESOURCE))) //
                                .add(Restrictions.eqProperty("stateGroup.id", "transitionStateGroup.id")))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("action.transitionAction", true)) //
                                .add(Restrictions.disjunction() //
                                        .add(Restrictions.eq("transitionStateGroup.repeatable", true)) //
                                        .add(Restrictions.conjunction() //
                                                .add(Restrictions.isNotNull("stateGroup.id")) //
                                                .add(Restrictions.isNotNull("transitionStateGroup.id")) //
                                                .add(Restrictions.neProperty("stateGroup.id", "transitionStateGroup.id"))) //
                                        .add(Restrictions.isNotNull("action.creationScope"))))) //
                .addOrder(Order.asc("createdTimestamp")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Comment> getStateComments(Resource resource, Comment start, Comment close, PrismStateGroup stateGroupId, List<Comment> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("action.visibleAction", true)) //
                        .add(Restrictions.neProperty("state.stateGroup", "transitionState.stateGroup"))) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("state.stateGroup.id", stateGroupId)) //
                        .add(Restrictions.isNull("state"))) //
                .add(Restrictions.ge("createdTimestamp", start.getCreatedTimestamp()));

        for (Comment exclusion : exclusions) {
            criteria.add(Restrictions.ne("id", exclusion.getId()));
        }

        if (close != null) {
            criteria.add(Restrictions.le("createdTimestamp", close.getCreatedTimestamp()));
        }

        return (List<Comment>) criteria.addOrder(Order.asc("createdTimestamp")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<CommentAssignedUser> getAssignedUsers(List<Integer> commentIds, List<PrismRole> roleIds) {
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .add(Restrictions.in("comment.id", commentIds)) //
                .add(Restrictions.in("role.id", roleIds)) //
                .add(Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

}
