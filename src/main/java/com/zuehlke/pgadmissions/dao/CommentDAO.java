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
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.Reviewer;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

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
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class)
        		.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ReviewComment> getReviewCommentsForReviewerAndApplication(Reviewer reviewer, ApplicationForm applicationForm) {
        return (List<ReviewComment>) sessionFactory.getCurrentSession().createCriteria(ReviewComment.class)
                .add(Restrictions.eq("reviewer", reviewer))
                .add(Restrictions.eq("application", applicationForm)).list();
    }

    public ValidationComment getValidationCommentForApplication(ApplicationForm applicationForm) {
        return (ValidationComment) sessionFactory.getCurrentSession().createCriteria(ValidationComment.class)
                .add(Restrictions.eq("type", CommentType.VALIDATION))
                .add(Restrictions.eq("application", applicationForm))
                .addOrder(Order.desc("date"))
                .setMaxResults(1).uniqueResult();
    }
    
}