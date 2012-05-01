package com.zuehlke.pgadmissions.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;

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

	public void save(Comment review) {
		sessionFactory.getCurrentSession().saveOrUpdate(review);
	}
	
	public Comment get(Integer id) {
		return (Comment) sessionFactory.getCurrentSession().get(
				Comment.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Comment> getReviewsByApplication(ApplicationForm application) {
		return sessionFactory.getCurrentSession()
				.createCriteria(Comment.class)
				.add(Restrictions.eq("application", application)).list();
	}
	
}
