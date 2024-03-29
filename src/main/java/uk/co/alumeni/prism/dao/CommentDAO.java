package uk.co.alumeni.prism.dao;

import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.comment.CommentAppointmentPreference;
import uk.co.alumeni.prism.domain.comment.CommentAssignedUser;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRoleTransitionType;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.APPLICATION_ASSIGN_HIRING_MANAGERS;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.APPLICATION_HIRING_MANAGER;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Comment getLatestComment(Resource resource, PrismAction... prismActions) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("action.id", prismActions)) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .addOrder(Order.desc("submittedTimestamp")) //
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
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .addOrder(Order.desc("submittedTimestamp")) //
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
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.ge("submittedTimestamp", baseline)) //
                .addOrder(Order.desc("submittedTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }

    public List<Comment> getUnsubmittedComments(Resource resource, Collection<PrismAction> prismActions, User user) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.in("action.id", prismActions)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("user", user)) //
                        .add(Restrictions.eq("delegateUser", user))) //
                .add(Restrictions.isNull("submittedTimestamp")) //
                .addOrder(Order.asc("action.id")) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public List<User> getAppointmentInvitees(Comment comment) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.groupProperty("user")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", Lists.newArrayList(PrismRole.APPLICATION_POTENTIAL_INTERVIEWEE, //
                        PrismRole.APPLICATION_POTENTIAL_INTERVIEWER, PrismRole.APPLICATION_INTERVIEWEE, PrismRole.APPLICATION_INTERVIEWER))) //
                .add(Restrictions.in("roleTransitionType", Arrays.asList(PrismRoleTransitionType.CREATE, PrismRoleTransitionType.BRANCH))) //
                .add(Restrictions.isNotNull("comment.submittedTimestamp")) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }

    public List<LocalDateTime> getAppointmentPreferences(Comment comment) {
        return (List<LocalDateTime>) sessionFactory.getCurrentSession().createCriteria(CommentAppointmentPreference.class) //
                .setProjection(Projections.property("dateTime")) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.isNotNull("comment.submittedTimestamp")) //
                .addOrder(Order.asc("dateTime")) //
                .list();
    }

    public List<CommentAssignedUser> getAssignedHiringManagers(Comment comment) {
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", Lists.newArrayList(APPLICATION_HIRING_MANAGER))) //
                .add(Restrictions.isNotNull("comment.submittedTimestamp")) //
                .addOrder(Order.asc("role.id")) //
                .addOrder(Order.asc("user.lastName")) //
                .addOrder(Order.asc("user.firstName")) //
                .list();
    }

    public List<String> getDeclinedHiringManagers(Comment comment) {
        return (List<String>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .setProjection(Projections.property("user.email")) //
                .createAlias("user", "user", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("action.id", APPLICATION_ASSIGN_HIRING_MANAGERS)) //
                .add(Restrictions.eq("recruiterAcceptAppointment", false)) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.ge("submittedTimestamp", comment.getSubmittedTimestamp())) //
                .list();
    }

    public List<Comment> getTimelineComments(Resource resource) {
        return sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("state", "state", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("state.stateGroup", "stateGroup", JoinType.INNER_JOIN) //
                .createAlias("transitionState", "transitionState", JoinType.LEFT_OUTER_JOIN) //
                .createAlias("transitionState.stateGroup", "transitionStateGroup", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.conjunction() //
                                .add(Restrictions.eq("action.transitionAction", true))
                                .add(Restrictions.disjunction() //
                                        .add(Restrictions.neProperty("stateGroup.id", "transitionStateGroup.id")) //
                                        .add(Restrictions.conjunction() //
                                                .add(Restrictions.isNotNull("transitionStateGroup.repeatable")) //
                                                .add(Restrictions.neProperty("state", "transitionState"))
                                                .add(Restrictions.ne("action.systemInvocationOnly", true))) //
                                        .add(Restrictions.isNotNull("action.creationScope"))))
                        .add(Restrictions.eq("action.visibleAction", true))) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .addOrder(Order.asc("submittedTimestamp")) //
                .addOrder(Order.asc("id")) //
                .list();
    }

    public List<User> getAssignedUsers(Comment comment, PrismRole... roles) {
        return (List<User>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("comment", comment)) //
                .add(Restrictions.in("role.id", roles)) //
                .add(Restrictions.isNotNull("comment.submittedTimestamp")) //
                .list();
    }

    public List<CommentAssignedUser> getAssignedUsers(List<Integer> commentIds, List<PrismRole> roleIds) {
        return (List<CommentAssignedUser>) sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .createAlias("comment", "comment", JoinType.INNER_JOIN) //
                .add(Restrictions.in("comment.id", commentIds)) //
                .add(Restrictions.in("role.id", roleIds)) //
                .add(Restrictions.eq("roleTransitionType", PrismRoleTransitionType.CREATE)) //
                .add(Restrictions.isNotNull("comment.submittedTimestamp")) //
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
                .add(Restrictions.isNotNull("submittedTimestamp")) //
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
                .add(Restrictions.isNotNull("comment.submittedTimestamp")) //
                .list();
    }

    public List<Comment> getComments(List<Integer> commentIds) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.in("id", commentIds)) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .addOrder(Order.asc("sequenceIdentifier")) //
                .list();
    }

    public List<Comment> getTransitionCommentHistory(Resource resource) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.neProperty("state", "transitionState")) //
                        .add(Restrictions.eq("action.replicableUserAssignmentAction", true))) //
                .add(Restrictions.isNotNull("submittedTimestamp")) //
                .add(Restrictions.eq("action.systemInvocationOnly", false)) //
                .addOrder(Order.desc("id")) //
                .list();
    }

    public List<Comment> getRatingComments(Resource resource) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq(resource.getResourceScope().getLowerCamelName(), resource)) //
                .add(Restrictions.eq("action.ratingAction", true)) //
                .list();
    }

    public List<Comment> getRatingComments(PrismScope scope, User user) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .createAlias(scope.getLowerCamelName(), "resource")
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .add(Restrictions.eq("resource.user", user)) //
                .add(Restrictions.eq("action.ratingAction", true)) //
                .addOrder(Order.asc("action.id")) //
                .list();
    }

}
