package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
import com.zuehlke.pgadmissions.domain.enums.CommentType;

@Repository
public class CommentDAO {

	private final SessionFactory sessionFactory;
	
	CommentDAO(){
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
		return (Comment) sessionFactory.getCurrentSession().get(
				Comment.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Comment> getAllComments(){
		return (List<Comment>) sessionFactory.getCurrentSession().createCriteria(Comment.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ReviewComment> getReviewCommentsDueNotification() {
		return (List<ReviewComment>) sessionFactory.getCurrentSession().createCriteria(ReviewComment.class)
				.add(Restrictions.eq("type", CommentType.REVIEW))
				.add(Restrictions.or(Restrictions.isNull("adminsNotified"), Restrictions.eq("adminsNotified",false)))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	@SuppressWarnings("unchecked")
	public List<InterviewComment> getInterviewCommentsDueNotification() {
		return (List<InterviewComment>) sessionFactory.getCurrentSession().createCriteria(InterviewComment.class)
				.add(Restrictions.eq("type", CommentType.INTERVIEW))
				.add(Restrictions.or(Restrictions.isNull("adminsNotified"), Restrictions.eq("adminsNotified",false)))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}
	
	
}
