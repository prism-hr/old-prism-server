package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Resource;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

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
    
    public Comment getLatestComment(Resource resource, Action action) {
        return (Comment) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(PrismScope.getResourceScope(resource.getClass()).getLowerCaseName(), resource)) //
                .add(Restrictions.eq("action", action)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .setMaxResults(1) //
                .uniqueResult();
    }
    
    public List<Comment> getComments(Resource resource) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq(resource.getClass().getSimpleName().toLowerCase(), resource)) //
                .addOrder(Order.desc("createdTimestamp")) //
                .list();
    }
    
    public List<Comment> getApplicationAssessmentComments(Application application) {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class) //
                .add(Restrictions.eq("application", application)) //
                .add(Restrictions.disjunction() //
                        .add(Restrictions.isNotNull("desireToInterview")) //
                        .add(Restrictions.isNotNull("desireToRecruit"))) //
                .addOrder(Order.asc("user")) //
                .addOrder(Order.desc("createdTimestamp")) //
                .addOrder(Order.desc("id")) //
                .list();
    }
    
}
