package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.User;

@Repository
@SuppressWarnings("unchecked")
public class CommentDAO {

    private final SessionFactory sessionFactory;

    public CommentDAO() {
        this(null);
    }

    @Autowired
    public CommentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Comment comment) {
        sessionFactory.getCurrentSession().saveOrUpdate(comment);
    }

    public Comment get(Integer id) {
        return (Comment) sessionFactory.getCurrentSession().get(Comment.class, id);
    }

    public List<Comment> getAllComments() {
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<Comment> getVisibleComments(User user, ApplicationForm applicationForm) {
        // TODO amend and add tests
        return sessionFactory
                .getCurrentSession()
                .createCriteria(Comment.class)
                .createAlias("application", "a")
                .createAlias("applicationFormUserRoles", "afur")
                .createAlias("afur.actions", "afar")
                .createAlias("role", "r")
                .createAlias("role.actions", "afao")
                .add(Restrictions.eq("application", applicationForm))
                .add(Restrictions.eq("afur.user", user))
                .add(Restrictions.disjunction().add(Restrictions.eq("user", user)).add(Restrictions.ge("r.internal", "afur.internal"))
                        .add(Restrictions.ge("r.internal", "afao.internal"))).addOrder(Order.desc("createdTimestamp")).list();
    }

    public List<CommentAssignedUser> getNotDecliningSupervisorsFromLatestApprovalStage(ApplicationForm application) {
        // TODO implement
        return null;
    }

    public <T extends Comment> T getLastCommentOfType(User user, ApplicationForm application, Class<T> clazz) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(clazz).add(Restrictions.eq("application", application))
                .addOrder(Order.desc("createdTimestamp")).addOrder(Order.desc("id"));
        if (user != null) {
            criteria.add(Restrictions.eq("user", user));
        }
        return (T) criteria.uniqueResult();
    }

    public Comment getById(int id) {
        // TODO Auto-generated method stub
        return null;
    }
}
