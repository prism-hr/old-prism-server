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
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
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
        return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ReviewComment> getReviewCommentsDueNotification() {
        return (List<ReviewComment>) sessionFactory.getCurrentSession().createCriteria(ReviewComment.class).add(Restrictions.eq("type", CommentType.REVIEW))
                .add(Restrictions.or(Restrictions.isNull("adminsNotified"), Restrictions.eq("adminsNotified", false)))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<InterviewComment> getInterviewCommentsDueNotification() {
        return (List<InterviewComment>) sessionFactory.getCurrentSession().createCriteria(InterviewComment.class)
                .add(Restrictions.eq("type", CommentType.INTERVIEW))
                .add(Restrictions.or(Restrictions.isNull("adminsNotified"), Restrictions.eq("adminsNotified", false)))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    public List<ReviewComment> getReviewCommentsForReviewerAndApplication(Reviewer reviewer, ApplicationForm applicationForm) {
        return (List<ReviewComment>) sessionFactory.getCurrentSession().createCriteria(ReviewComment.class).add(Restrictions.eq("type", CommentType.REVIEW))
                .add(Restrictions.eq("reviewer", reviewer)).add(Restrictions.eq("application", applicationForm)).list();
    }

    public ValidationComment getValidationCommentForApplication(ApplicationForm applicationForm) {
        return (ValidationComment) sessionFactory.getCurrentSession().createCriteria(ValidationComment.class) //
                .add(Restrictions.eq("type", CommentType.VALIDATION)) //
                .add(Restrictions.eq("application", applicationForm)) //
                .addOrder(Order.desc("date"))
                .setMaxResults(1)
                .uniqueResult();
    }

    public List<Comment> getVisibleComments(RegisteredUser user, ApplicationForm applicationForm) {
        // TODO amend and add tests
        return sessionFactory.getCurrentSession().createCriteria(Comment.class)
                .createAlias("application", "a")
                .createAlias("applicationFormUserRoles", "afur")
                .createAlias("afur.actions", "afar")
                .createAlias("role", "r")
                .createAlias("role.actions", "afao")
                .add(Restrictions.eq("application", applicationForm))
                .add(Restrictions.eq("afur.user", user))
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq("user", user))
                        .add(Restrictions.ge("r.internal", "afur.internal"))
                        .add(Restrictions.ge("r.internal", "afao.internal")))
                .addOrder(Order.desc("createdTimestamp"))
                .list();
    }
}
