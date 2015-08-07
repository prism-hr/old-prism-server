package com.zuehlke.pgadmissions.dao;

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
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Comment getLatestComment(Resource resource) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getByResourceClass(resource.getClass()).getLowerCamelName(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, PrismAction... prismActions) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getByResourceClass(resource.getClass()).getLowerCamelName(), resource)) //
                .add(Restrictions.in("action.id", prismActions)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, User user, PrismAction... prismActions) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getByResourceClass(resource.getClass()).getLowerCamelName(), resource)) //
                .add(Restrictions.in("action.id", prismActions)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("user", user)) //
                        .add(Restrictions.eq("delegateUser", user))) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, PrismAction prismAction, DateTime baseline) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getByResourceClass(resource.getClass()).getLowerCamelName(), resource)) //
                .add(Restrictions.eq("action.id", prismAction)) //
                .add(Restrictions.ge("createdTimestamp", baseline)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, PrismAction prismAction, User user, DateTime baseline) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getByResourceClass(resource.getClass()).getLowerCamelName(), resource)) //
                .add(Restrictions.eq("action.id", prismAction)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("user", user)) //
                        .add(Restrictions.eq("delegateUser", user))) //
                .add(Restrictions.ge("createdTimestamp", baseline)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public <T extends Resource> Comment getEarliestComment(ResourceParent parentResource, Class<T> resourceClass, PrismAction actionId) {
        String resourceReference = PrismScope.getByResourceClass(resourceClass).getLowerCamelName();
        String parentResourceReference = parentResource.getResourceScope().getLowerCamelName();
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

    public List<LocalDateTime> getAppointmentPreferences(Comment comment) {
        return (List<LocalDateTime>) sessionFactory.getCurrentSession().createCriteria(CommentAppointmentPreference.class) //
                .setProjection(Projections.property("dateTime")) //
                .add(Restrictions.eq("comment", comment)) //
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

    public List<String> getDeclinedSupervisors(Comment comment) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("user.email")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.ge("createdTimestamp", comment.getCreatedTimestamp())) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_ASSIGN_SUPERVISORS)) //
                .add(Restrictions.eq("recruiterAcceptAppointment", false)) //
                .list();
    }

    public List<Comment> getRecentComments(PrismScope resourceScope, Integer resourceId, DateTime rangeStart, DateTime rangeClose) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resourceScope.getLowerCamelName() + ".id", resourceId)) //
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
                .add(Restrictions.isNotNull("action.transitionAction")) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.neProperty("stateGroup.id", "transitionStateGroup.id")) //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.ne("action.systemInvocationOnly", true))
                                .add(Restrictions.isNotNull("transitionStateGroup.repeatable")) //
                                .add(Restrictions.neProperty("state", "transitionState"))) //
                        .add(Restrictions.isNotNull("action.creationScope"))) //
                .addOrder(Order.asc("createdTimestamp")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<Comment> getStateComments(Resource resource, Comment start, Comment close, StateGroup stateGroup, List<Comment> exclusions) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.LEFT_OUTER_JOIN) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .add(Restrictions.eq("action.visibleAction", true)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("state.stateGroup", stateGroup)) //
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

    public List<User> getAssignedUsers(Comment comment, PrismRole... roles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", roles)) //
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

    public void reassignComments(User oldUser, User newUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Comment "
                        + "set user = :newUser "
                        + "where user = :oldUser") //
                .setParameter("newUser", newUser) //
                .setParameter("oldUser", oldUser) //
                .executeUpdate();
    }

    public void reassignDelegateComments(User oldUser, User newUser) {
        sessionFactory.getCurrentSession().createQuery( //
                "update Comment "
                        + "set delegateUser = :newUser "
                        + "where delegateUser = :oldUser") //
                .setParameter("newUser", newUser) //
                .setParameter("oldUser", oldUser) //
                .executeUpdate();
    }

    public List<CommentAssignedUser> getCommentAssignedUsers(User user) {
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .add(Restrictions.eq("user", user)) //
                .list();
    }

}
