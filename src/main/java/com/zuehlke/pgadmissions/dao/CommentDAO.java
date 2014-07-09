package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.User;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    public Comment getLastComment(Resource resource) {
        return getLastCommentOfType(resource, Comment.class, null);
    }
    
    public <T extends Comment> T getLastCommentOfType(Resource resource, Class<T> clazz) {
        return getLastCommentOfType(resource, clazz, null);
    }
    
    public <T extends Comment> T getLastCommentOfType(Resource resource, Class<T> clazz, User author) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(clazz) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id"));
        
        if (author != null) {
            criteria.add(Restrictions.eq("user", author));
        }
        
        return (T) criteria.uniqueResult();
    }
    
    public List<Comment> getComments(Resource resource) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .list();
    }

}
