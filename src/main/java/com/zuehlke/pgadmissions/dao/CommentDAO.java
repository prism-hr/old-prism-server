package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.StateAction;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismActionType;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public Comment getLastComment(PrismResourceDynamic resource) {
        return getLastCommentOfType(resource, Comment.class, null);
    }
    
    public <T extends Comment> T getLastCommentOfType(PrismResourceDynamic resource, Class<T> clazz) {
        return getLastCommentOfType(resource, clazz, null);
    }
    
    public <T extends Comment> T getLastCommentOfType(PrismResourceDynamic resource, Class<T> clazz, User author) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(clazz) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id"));
        
        if (author != null) {
            criteria.add(Restrictions.eq("user", author));
        }
        
        return (T) criteria.uniqueResult();
    }
    
    public List<User> getAssignedUsers(Comment comment, Role role, User invoker) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CommentAssignedUser.class) //
                .setProjection(Projections.property("user")) //
                .add(Restrictions.eq("comment", comment));
        
        if (invoker != null) {
            criteria.add(Restrictions.eq("user", invoker));
        }
                
        return (List<User>) criteria.add(Restrictions.eq("role", role)).list();
    }
    
    public List<Comment> getVisibleComments(User user, PrismResourceDynamic resource) {
        String resourceName = resource.getClass().getSimpleName().toLowerCase();
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(StateAction.class)
                .setProjection(Projections.property(resourceName + ".comments")) //
                .createAlias("action", "action", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignments", "stateActionAssignment", JoinType.INNER_JOIN) //
                .createAlias("stateActionAssignment.role", "role", JoinType.INNER_JOIN) //
                .createAlias("role.userRoles", "userRole", JoinType.INNER_JOIN) //
                .createAlias("userRole.user", "user", JoinType.INNER_JOIN) //
                .createAlias("user.userAccount", "userAccount", JoinType.INNER_JOIN) //
                .createAlias("userRole." + resourceName, resourceName, JoinType.INNER_JOIN)
                .createAlias(resourceName + ".comments", "comment", JoinType.INNER_JOIN)
                .add(Restrictions.eq("state", resource.getState())) //
                .add(Restrictions.eq("action.actionType", PrismActionType.USER_INVOCATION)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.eq("userRole.application", resource.getApplication())) //
                        .add(Restrictions.eq("userRole.project", resource.getProject())) //
                        .add(Restrictions.eq("userRole.program", resource.getProgram())) //
                        .add(Restrictions.eq("userRole.institution", resource.getInstitution())) //
                        .add(Restrictions.eq("userRole.system", resource.getSystem()))) //
                .add(Restrictions.eq("user.parentUser", user)) //
                .add(Restrictions.eq("userAccount.enabled", true)) //
                .addOrder(Order.desc("comment.createdTimestamp"))
                .list();
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(Application application) {
        // TODO implement
        return null;
    }

}
