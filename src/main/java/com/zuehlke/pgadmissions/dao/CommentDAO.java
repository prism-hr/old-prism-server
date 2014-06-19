package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.PrismResourceDynamic;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.User;

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
    
    public List<Comment> getComments(PrismResourceDynamic resource) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .list();
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(Application application) {
        // TODO implement
        return null;
    }

}
