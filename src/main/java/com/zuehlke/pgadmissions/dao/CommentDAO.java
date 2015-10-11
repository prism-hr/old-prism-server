package com.zuehlke.pgadmissions.dao;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAppointmentPreference;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
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

import java.util.Arrays;
import java.util.List;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Comment getLatestComment(Resource resource, PrismAction... prismActions) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("action.id", prismActions)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, User user, PrismAction... prismActions) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("action.id", prismActions)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("user", user)) //
                        .add(Restrictions.eq("delegateUser", user))) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public Comment getLatestComment(Resource resource, PrismAction prismAction, User user, DateTime baseline) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
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

    public List<CommentAssignedUser> getAssignedHiringManagers(Comment comment) {
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", Lists.newArrayList(APPLICATION_HIRING_MANAGER))) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }

    public List<String> getDeclinedHiringManagers(Comment comment) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("user.email")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.ge("createdTimestamp", comment.getCreatedTimestamp())) //
                .add(Restrictions.eq("action.id", PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS)) //
                .add(Restrictions.eq("recruiterAcceptAppointment", false)) //
                .list();
    }

    public List<Comment> getTimelineComments(Resource resource) {
        return getCommentTimelineCriteria() //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.isNotNull("action.transitionAction"))
                                .add(Restrictions.disjunction() //
                                        .add(Restrictions.neProperty("stateGroup.id", "transitionStateGroup.id")) //
                                        .add(Restrictions.conjunction() //
                                                .add(Restrictions.isNotNull("transitionStateGroup.repeatable")) //
                                                .add(Restrictions.neProperty("state", "transitionState"))
                                                .add(Restrictions.ne("action.systemInvocationOnly", true))) //
                                        .add(Restrictions.isNotNull("action.creationScope"))))
                        .add(Restrictions.eq("action.visibleAction", true))) //
                .addOrder(Order.asc("createdTimestamp")) //
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

    public List<Comment> getResourceOwnerComments(Resource resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias(resourceReference, resourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReference, resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eqProperty("user", resourceReference + ".user")) //
                        .add(Restrictions.eqProperty("delegateUser", resourceReference + ".user"))) //
                .list();
    }

    public List<CommentAssignedUser> getResourceOwnerCommentAssignedUsers(Resource resource) {
        String resourceReference = resource.getResourceScope().getLowerCamelName();
        String resourceReferenceComment = "comment." + resourceReference;
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .createAlias(resourceReferenceComment, resourceReference, JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resourceReferenceComment, resource)) //
                .add(Restrictions.eqProperty("user", resourceReference + ".user")) //
                .list();
    }

    private Criteria getCommentTimelineCriteria() {
        return sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN);
    }

}
